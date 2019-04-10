package com.hanlin.fadp.petrescence.datasource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.rmi.CORBA.Util;
import javax.xml.parsers.ParserConfigurationException;

import com.hanlin.fadp.service.job.JobPoller;
import javolution.util.FastMap;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import com.hanlin.fadp.base.config.GenericConfigException;
import com.hanlin.fadp.base.crypto.SimpleEncode;
import com.hanlin.fadp.base.location.ComponentLocationResolver;
import com.hanlin.fadp.base.start.Start;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.FileUtil;
import com.hanlin.fadp.base.util.UtilDateTime;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.GenericEntityConfException;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.config.model.Datasource;
import com.hanlin.fadp.entity.config.model.DelegatorElement;
import com.hanlin.fadp.entity.config.model.EntityConfig;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.datasource.GenericDAO;
import com.hanlin.fadp.entity.datasource.GenericHelperInfo;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.service.DispatchContext;
import com.hanlin.fadp.service.ServiceUtil;
import com.hanlin.fadp.service.job.JobManager;

public class DataSourceWorker {
	private static String backupPath = "component://entityMgr/backup";
	private static String templatePath = "component://entityMgr/template";
	private static String entityengineXml = "component://entity/config/entityengine.xml";
	private static String componentXml = "component://entityMgr/fadp-component.xml";
	static String entityModelXml_pre = "component://entityMgr/entitydef/";

	public static Map<String, Object> updateDataSource(DispatchContext ctx, Map<String, Object> context) throws Exception{
		Delegator delegator = ctx.getDelegator();
		GenericValue ds = delegator.findOne("DatabaseSeq", false, "dataSourceName",
				context.get("dataSourceName"));
		if (ds == null) {
			return UtilMisc.toMap("errorMessage", "数据库错误,该数据源已近丢失");
		}
		delegator.removeValue(ds);
		return addDataSource(ctx, context);

	}

	public static Map<String, Object> deleteDataSource(DispatchContext ctx, Map<String, Object> context) throws Exception,
			IOException, GenericEntityException {
		boolean modified = false;
		GenericValue value= (GenericValue) context.get("datasource");
		File engineXml = FileUtil.getFile(entityengineXml);
		Document engine = UtilXml.readXmlDocument(engineXml.toURI().toURL(),false);
		String dataSourceName =  value.getString("dataSourceName");
		//删除当前数据源的jobManager
		Field jobManagersField = JobPoller.class.getDeclaredField("jobManagers");
		jobManagersField.setAccessible(true);

		ConcurrentHashMap<String, JobManager> jobManagers = (ConcurrentHashMap<String, JobManager>) jobManagersField.get(JobPoller.getInstance());
		jobManagers.remove(dataSourceName);

		for (Element childElement : UtilXml.childElementList(engine.getDocumentElement())) {
			String name = childElement.getAttribute("name");
			if (UtilValidate.isNotEmpty(name) && UtilValidate.isNotEmpty(dataSourceName)) {
				if (name.equals(dataSourceName) || name.equals("local" + dataSourceName)) {
					modified = true;
					engine.getDocumentElement().removeChild(childElement);
				}
			}

		}



		if (modified) {
			UtilXml.writeXmlDocument(engineXml.getCanonicalPath(), engine);
			File entityDefFile = FileUtil.getFile(entityModelXml_pre + dataSourceName + ".xml");
			if (entityDefFile.exists()) {
				entityDefFile.delete();
			}
			File entityGroupDefFile = FileUtil
					.getFile(entityModelXml_pre + dataSourceName + "-group.xml");
			if (entityGroupDefFile.exists()) {
				entityGroupDefFile.delete();
			}
		}

		return new FastMap<String, Object>();

	}

	public static Map<String, Object> checkDataSource(DispatchContext ctx, Map<String, Object> context)
			throws GenericConfigException, GenericEntityConfException {
		List<DelegatorElement> delegatorList = EntityConfig.getInstance().getDelegatorList();
		for (DelegatorElement delegatorInfo : delegatorList) {
			if ("default".equals(delegatorInfo.getName())) {
				continue;
			}
			DelegatorFactory.getDelegator(delegatorInfo.getName());
			Debug.log("检查delegator:" + delegatorInfo.getName() + "完毕");
		}
		return new FastMap<String, Object>();
	}

	public static Map<String, Object> addDataSource(DispatchContext ctx, Map<String, Object> context) {
		try {
			TransactionUtil.commit();
			return UtilMisc.toMap("msg", doIt(ctx, context));
		} catch (Exception e) {
			Debug.logError(e,"DataSourceWorker");
			return ServiceUtil.returnError(e.getMessage());
		}
	}

	private static String doIt(DispatchContext ctx, Map<String, Object> context) throws Exception {
		File backup = FileUtil.getFile(backupPath);
		if (backup == null || !backup.exists()) {
			backup = new File(ComponentLocationResolver.getBaseLocation(backupPath).toString());
			backup.mkdir();
		}
		String sufx = UtilDateTime.toDateString(UtilDateTime.nowDate(), "yyyyMMddHHmmss") + ".xml";
		File backupFile = new File(backup, "entityengine" + sufx);
		File engineXml = FileUtil.getFile(entityengineXml);
		Files.copy(engineXml.toPath(), backupFile.toPath());
		Files.copy(FileUtil.getFile(componentXml).toPath(), new File(backup, "component" + sufx).toPath());

		if (backupFile.exists()) {
			String field_type_name = (String) context.get("field-type-name");

			//获取模版entityengine.xml
			Element templateDatasource = getTemplateDatasource(field_type_name);

			if (templateDatasource == null) {
				throw new Exception("没有该数据源的模板,无法创建该数据源:" + field_type_name);
			}

			Document engine = UtilXml.readXmlDocument(engineXml.toURI().toURL(),false);
			Element rootElement = engine.getDocumentElement();
			Delegator delegator = ctx.getDelegator();
			String name= "db" + delegator.getNextSeqId("DatabaseSeq");
			context.put("dataSourceName", name);
			Comment comment = engine.createComment("*************这是一个新的数据源*" + name + "****************************");
			rootElement.appendChild(comment);
			addResourceLoaderEngineXml(engine,rootElement);
			addDatasourceToEntineXml(name, ctx, context, engine, rootElement, templateDatasource);
			comment = engine.createComment("*************数据源*" + name + "*配置结束***************************");
			rootElement.appendChild(comment);
			context.put("isShow","Y");
			GenericValue database = delegator.makeValidValue("DatabaseSeq", context);

			delegator.createOrStore(database);

//			addToComponent("entitymodel_localsys", name, "model");
//			addToComponent(name, name, "model");
//
//
//			addToComponent(name + "-group", name, "group");
			UtilXml.writeXmlDocument(engineXml.getCanonicalPath(), engine);

			EntityEngineUtil.reInitEntityConfig();
			makeEntityModelXml(name);

		} else {
			throw new IOException("无法备份entityengine.xml文件");
		}
		return "成功添加数据源";
	}

	public static Element getTemplateDatasource(String field_type_name) throws Exception {
		//获取模版entityengine.xml
		Document templateEngine = UtilXml.readXmlDocument(FileUtil.getFile(templatePath + "/entityengine.xml").toURI().toURL(),false);
		Element templateDatasource = UtilXml.firstChildElement(templateEngine.getDocumentElement(), "datasource", "name", "local" + field_type_name);

		return templateDatasource;

	}


	private static void addResourceLoaderEngineXml(Document engine, Element rootElement){
		Element resourceLoader = UtilXml.firstChildElement(rootElement, "resource-loader", "prefix", "/hot-deploy/entityMgr/entitydef/");
		if(resourceLoader==null){

			Element element= engine.createElement("resource-loader");


			element.setAttribute("class","com.hanlin.fadp.base.config.FileLoader");
			element.setAttribute("name","main");
			element.setAttribute("prefix","/hot-deploy/entityMgr/entitydef/");
			element.setAttribute("prepend-env","fadp.home");
			Element firstResourceLoader = UtilXml.firstChildElement(rootElement, "resource-loader");
			rootElement.insertBefore(element,firstResourceLoader);
		}
	}
	private static void addDatasourceToEntineXml(String name, DispatchContext ctx, Map<String, Object> context,
												 Document engine, Element rootElement, Element oldDatasourceElement)
			throws IOException, GenericEntityException, SAXException, ParserConfigurationException {
		if (context.get("schema-name") == null) {
			context.put("schema-name", name);
		}
		if (context.get("check-on-start") == null) {
			context.put("check-on-start", "true");
		}
		if (context.get("add-missing-on-start") == null) {
			context.put("add-missing-on-start", "true");
		}
		makeStaticXml(name, rootElement, engine);// 添加delegator model-reader
		// group-reader eca-reader
		Element dataSourceElement = UtilXml.addChildElement(rootElement, "datasource", engine);
		Element oldEle = oldDatasourceElement;
		NamedNodeMap attributes = oldEle.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node item = attributes.item(i);
			String attrValue = null;
			if (context.get(item.getNodeName()) != null) {
				attrValue = (String) context.get(item.getNodeName());
			} else {
				attrValue = item.getNodeValue();
			}
			dataSourceElement.setAttribute(item.getNodeName(), attrValue);
		}

		dataSourceElement.setAttribute("name", name);
		Element inlineJdbcElement = UtilXml.addChildElement(dataSourceElement, "inline-jdbc", engine);
		attributes = UtilXml.firstChildElement(oldEle, "inline-jdbc").getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node item = attributes.item(i);
			String attrValue = null;
			if (context.get(item.getNodeName()) != null) {
				attrValue = (String) context.get(item.getNodeName());
			} else {
				attrValue = item.getNodeValue();
			}
			if(item.getNodeName().equals("jdbc-password")){
				//TODO:若需要加密,放开
//				attrValue=SimpleEncode.encode(attrValue, Start.getInstance().pass);
			}
			inlineJdbcElement.setAttribute(item.getNodeName(), attrValue);
		}

	}

	private static void makeStaticXml(String name, Element rootElement, Document engine) {


		Element element = UtilXml.addChildElement(rootElement,"delegator",engine);
		Element defaultDelegatorElement = UtilXml.firstChildElement(rootElement, "delegator", "name", "default");
		Element groupElement = UtilXml.firstChildElement(defaultDelegatorElement, "group-map");
		String defaultDatasourceName = groupElement.getAttribute("datasource-name");
		String defaultGroupName = groupElement.getAttribute("group-name");
//		rootElement.insertBefore(element, UtilXml.firstChildElement(rootElement, "entity-model-reader"));
		element.setAttribute("default-group-name", defaultGroupName);
		element.setAttribute("distributed-cache-clear-class-name", "com.hanlin.fadp.entityext.cache.EntityCacheServices");
		element.setAttribute("entity-eca-handler-class-name", "com.hanlin.fadp.entityext.eca.DelegatorEcaHandler");
		element.setAttribute("entity-eca-reader", name);
		element.setAttribute("entity-group-reader", name);
		element.setAttribute("entity-model-reader", name);
		element.setAttribute("name", name);
		// String defaultDatasourceName="localderby";
		addGroupToDelegator(defaultDatasourceName, defaultGroupName, element, engine);
		addGroupToDelegator(name, name, element, engine);
		//创建entity-model-reader
		element = UtilXml.addChildElement(rootElement,"entity-model-reader",engine);
//		element = engine.createElement("entity-model-reader");
//		rootElement.insertBefore(element, UtilXml.firstChildElement(rootElement, "entity-group-reader"));
		element.setAttribute("name", name);
		//向reader中添加resource
		Element childElement = UtilXml.addChildElement(element, "resource", engine);
		childElement.setAttribute("loader","main");
		childElement.setAttribute("location",name+".xml");
		childElement = UtilXml.addChildElement(element, "resource", engine);
		childElement.setAttribute("loader","main");
		childElement.setAttribute("location","entitymodel_localsys.xml");

		//创建entity-group-reader
		element = UtilXml.addChildElement(rootElement,"entity-group-reader",engine);
//		element = engine.createElement("entity-group-reader");
//		rootElement.insertBefore(element, UtilXml.firstChildElement(rootElement, "entity-eca-reader"));
		element.setAttribute("name", name);
		//向reader中添加resource
		childElement = UtilXml.addChildElement(element, "resource", engine);
		childElement.setAttribute("loader","main");
		childElement.setAttribute("location",name+"-group.xml");

		//创建entity-eca-reader
		element = UtilXml.addChildElement(rootElement,"entity-eca-reader",engine);
//		element = engine.createElement("entity-eca-reader");
//		rootElement.insertBefore(element, UtilXml.firstChildElement(rootElement, "entity-data-reader"));
		element.setAttribute("name", name);

	}


	private static void addGroupToDelegator(String name, String groupName, Element element, Document engine) {
		Element childElement = UtilXml.addChildElement(element, "group-map", engine);
		childElement.setAttribute("datasource-name", name);
		childElement.setAttribute("group-name", groupName);
	}

	private static void addToComponent(String name, String reader, String type)
			throws MalformedURLException, SAXException, ParserConfigurationException, IOException {
		Document componentDocument = UtilXml.readXmlDocument(FileUtil.getFile(componentXml).toURI().toURL(),false);
		Element element = componentDocument.createElement("entity-resource");
		componentDocument.getDocumentElement().insertBefore(element,
				UtilXml.firstChildElement(componentDocument.getDocumentElement(), "service-resource"));
		element.setAttribute("type", type);
		element.setAttribute("reader-name", reader);
		element.setAttribute("loader", "main");
		element.setAttribute("location", "entitydef/" + name + ".xml");

		UtilXml.writeXmlDocument(FileUtil.getFile(componentXml).getCanonicalPath(), componentDocument);
	}

	private static void makeEntityModelXml(String name)
			throws Exception {
		TransactionUtil.commit();
		GenericDAO dao = GenericDAO.getGenericDAO(new GenericHelperInfo(name, name));
		Collection<String> messages = new LinkedList<>();
		List<ModelEntity> modelFromDb=new ArrayList<>();
		try{
			modelFromDb = dao.induceModelFromDb(messages);

		}catch (Exception e){
			System.err.println("e = " + e.getMessage());
		}
		File entityModelFile = copyFile(name, "template");
		File entityModelGroupFile = copyFile(name + "-group", "template-group");
		Document entityModelDocument = UtilXml.readXmlDocument(entityModelFile.toURI().toURL(),false);
		Document groupDocument = UtilXml.readXmlDocument(entityModelGroupFile.toURI().toURL(),false);
		Element groupRootElement = groupDocument.getDocumentElement();
		for (ModelEntity modelEntity : modelFromDb) {
			modelEntity.setNoAutoStamp(true);
			Element entityGroupElement = UtilXml.addChildElement(groupRootElement, "entity-group", groupDocument);
			entityGroupElement.setAttribute("entity", modelEntity.getEntityName());
			entityGroupElement.setAttribute("group", name);
			EntityEngineUtil.correctFieldType(modelEntity);
			entityModelDocument.getDocumentElement().appendChild(modelEntity.toXmlElement(entityModelDocument));
		}
		UtilXml.writeXmlDocument(entityModelFile.getCanonicalPath(), entityModelDocument);
		UtilXml.writeXmlDocument(entityModelGroupFile.getCanonicalPath(), groupDocument);
		Debug.log("生成entity定义文件" + modelFromDb.size());
	}

	private static File copyFile(String newFileName, String templateFileName) throws IOException {
		File file = new File(
				ComponentLocationResolver.getBaseLocation(entityModelXml_pre + newFileName + ".xml").toString());
		Files.copy(FileUtil.getFile(entityModelXml_pre + templateFileName + ".xml").toPath(), file.toPath());
		return file;
	}

}
