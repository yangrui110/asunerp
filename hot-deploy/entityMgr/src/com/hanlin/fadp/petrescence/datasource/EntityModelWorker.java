package com.hanlin.fadp.petrescence.datasource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.hanlin.fadp.base.location.ComponentLocationResolver;
import com.hanlin.fadp.base.util.UtilHttp;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.UtilXml;

public class EntityModelWorker {
	public static List<Map<String, String>> readEntityModelListFromXML()
			throws SAXException, ParserConfigurationException, IOException {
		return readEntityModelListFromXML(UtilMisc.toMap());
	}

	public static List<Map<String, String>> readEntityModelListFromXML(Map<String, Object> parameters)
			throws SAXException, ParserConfigurationException, IOException {
		// parameters.put("entityModelXMLName",
		// parameters.get("dataSourceName"));
		File entityModelXMLFile = getEntityModelXMLFile(parameters);
		Document document = UtilXml.readXmlDocument(entityModelXMLFile.toURI().toURL(),false);
		List<? extends Element> childElementList = UtilXml.childElementList(document.getDocumentElement(), "entity");
		if (childElementList == null) {
			return null;
		}
		List<Map<String, String>> list = new FastList<Map<String, String>>();
		for (Element element : childElementList) {
			Map<String, String> map = new FastMap<String, String>();
			map.put("entityName", element.getAttribute("entity-name"));
			map.put("title", element.getAttribute("title"));
			Element entityDescriptionElement = UtilXml.firstChildElement(element, "description");
			if (entityDescriptionElement != null) {
				map.put("description", entityDescriptionElement.getTextContent());
			} else {
				map.put("description", "");
			}
			list.add(map);
		}
		return list;
	}

	public static Map<String, Object> readEntityModelFromXML(String dataSourceName, String entityName)
			throws SAXException, ParserConfigurationException, IOException {
		File entityModelXMLFile = getEntityModelXMLFile(UtilMisc.toMap("entityModelXMLName", (Object) dataSourceName));
		Document document = UtilXml.readXmlDocument(entityModelXMLFile.toURI().toURL(),false);
		Element entityElement = UtilXml.firstChildElement(document.getDocumentElement(), "entity", "entity-name",
				entityName);
		if (entityElement == null) {
			return null;
		}
		Map<String, Object> map = UtilMisc.toMap();
		// map.put("no-auto-stamp",true);
		map.put("entity-name", entityElement.getAttribute("entity-name"));
		map.put("package-name", entityElement.getAttribute("package-name"));
		map.put("title", entityElement.getAttribute("title"));
		// map.put("title", entityElement.getAttribute("title"));
		Element entityDescriptionElement = UtilXml.firstChildElement(entityElement, "description");
		if (entityDescriptionElement != null) {
			map.put("entity-description", entityDescriptionElement.getTextContent());
		}
		List<? extends Element> pkList = UtilXml.childElementList(entityElement, "prim-key");
		// List<Element> pkList = new FastList<>();
		// List<Element> fieldList = new FastList<>();
		// List<Element> relationList = new FastList<>();
		// List<? extends Element> elementList =
		// UtilXml.childElementList(entityElement);
		// for (Element element : elementList) {
		// if ("prim-key".equals(element.getNodeName())) {
		// pkList.add(element);
		// } else if ("field".equals(element.getNodeName())) {
		// fieldList.add(element);
		// } else if ("relation".equals(element.getNodeName())) {
		// relationList.add(element);
		// }
		// }
		HashSet<String> pkSet = new HashSet<>();
		if (pkList != null && pkList.size() > 0) {
			map.put("prim-key", new String[pkList.size()]);
			for (int i = 0; i < pkList.size(); i++) {
				pkSet.add(pkList.get(i).getAttribute("field"));
			}
		}

		List<? extends Element> fieldList = UtilXml.childElementList(entityElement, "field");
		if (fieldList != null && fieldList.size() > 0) {
			map.put("field-name", new String[fieldList.size()]);
			map.put("field-type", new String[fieldList.size()]);
			map.put("field-not-null", new String[fieldList.size()]);
			map.put("prim-key", new String[fieldList.size()]);
			map.put("field-description", new String[fieldList.size()]);
			for (int i = 0; i < fieldList.size(); i++) {
				((String[]) map.get("field-name"))[i] = fieldList.get(i).getAttribute("name");
				((String[]) map.get("field-type"))[i] = fieldList.get(i).getAttribute("type");
				if (pkSet.contains(fieldList.get(i).getAttribute("name"))) {
					((String[]) map.get("prim-key"))[i] = "是";
				} else {
					((String[]) map.get("prim-key"))[i] = "否";
				}
				if ("true".equals(fieldList.get(i).getAttribute("not-null"))) {
					((String[]) map.get("field-not-null"))[i] = "是";
				} else {
					((String[]) map.get("field-not-null"))[i] = "否";
				}
				Element fieldDescriptionElement = UtilXml.firstChildElement(fieldList.get(i), "description");
				if (fieldDescriptionElement != null) {
					((String[]) map.get("field-description"))[i] = fieldDescriptionElement.getTextContent();
				} else {
					((String[]) map.get("field-description"))[i] = "";
				}

			}

		}

		List<? extends Element> relationList = UtilXml.childElementList(entityElement, "relation");
		if (relationList != null && relationList.size() > 0) {
			map.put("fk-name", new String[relationList.size()]);
			map.put("fk-type", new String[relationList.size()]);
			map.put("rel-entity-name", new String[relationList.size()]);
			for (int i = 0; i < relationList.size(); i++) {
				Element relationElement = relationList.get(i);
				String fkName = relationElement.getAttribute("fk-name");
				((String[]) map.get("fk-name"))[i] = fkName;
				((String[]) map.get("fk-type"))[i] = relationElement.getAttribute("type");
				((String[]) map.get("rel-entity-name"))[i] = relationElement.getAttribute("rel-entity-name");
				List<? extends Element> keyMapList = UtilXml.childElementList(relationElement, "key-map");
				if (keyMapList != null && keyMapList.size() > 0) {
					map.put("key-map-" + fkName + "-field-name", new String[keyMapList.size()]);
					map.put("key-map-" + fkName + "-rel-field-name", new String[keyMapList.size()]);
					for (int j = 0; j < keyMapList.size(); j++) {
						Element keyMapElement = keyMapList.get(j);
						((String[]) map.get("key-map-" + fkName + "-field-name"))[j] = keyMapElement
								.getAttribute("field-name");
						String relFieldName = keyMapElement.getAttribute("rel-field-name");
						if (relFieldName == null || "".equals(relFieldName)) {
							relFieldName = "_NONE_";
						}
						((String[]) map.get("key-map-" + fkName + "-rel-field-name"))[j] = relFieldName;

					}
				}
			}
		}
		System.out.println(JSONObject.fromObject(map));
		return map;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> addEntityModel(Map<String, Object> parameters)
			throws Exception {
		File file = getEntityModelXMLFile(parameters);
		Document document = UtilXml.readXmlDocument(file.toURI().toURL(),false);
		Element rootElement = document.getDocumentElement();
		Element entityElement = UtilXml.addChildElement(rootElement, "entity", document);
		String entityName = (String) parameters.get("new-entity-name");
		entityElement.setAttribute("entity-name", entityName);
		entityElement.setAttribute("package-name", "");
		entityElement.setAttribute("no-auto-stamp", "true");
		entityElement.setAttribute("title", (String) parameters.get("title"));
		// entityElement.setAttribute("fuck", (String) parameters.get("title"));
		UtilXml.addChildElement(entityElement, "description", document)
				.setTextContent((String) parameters.get("entity-description"));

		Set<String> pkSet = new HashSet<>();
		// 字段
		List<Map<String, Object>> fieldData = (List<Map<String, Object>>) parameters.get("fieldData");
		if (UtilValidate.isNotEmpty(fieldData)) {
			for (Map<String, Object> field : fieldData) {
				Element fieldElement = UtilXml.addChildElement(entityElement, "field", document);
				fieldElement.setAttribute("name", field.get("field-name") + "");
				fieldElement.setAttribute("type", field.get("field-type") + "");
				if ("是".equals(field.get("prim-key") + "")) {
					pkSet.add(field.get("field-name") + "");
				}
				String description = field.get("field-description") + "";
				if (description != null && !"".equals(description)) {
					Element descriptionElement = UtilXml.addChildElement(fieldElement, "description", document);
					descriptionElement.setTextContent(description);
				}
			}
		}

		// 主键
		if (pkSet.size() != 0) {
			for (String field : pkSet) {
				Element fieldElement = UtilXml.addChildElement(entityElement, "prim-key", document);
				fieldElement.setAttribute("field", field);
			}

		}
		if (UtilValidate.isNotEmpty(fieldData)) {
			List<Map<String, Object>> fkData = (List<Map<String, Object>>) parameters.get("fkData");
			for (Map<String, Object> field : fkData) {
				Element fieldElement = UtilXml.addChildElement(entityElement, "relation", document);
				String fkName = field.get("fk-name") + "";
				fieldElement.setAttribute("fk-name", fkName);
				fieldElement.setAttribute("type", field.get("fk-type") + "");
				fieldElement.setAttribute("rel-entity-name", field.get("rel-entity-name") + "");
				Element keyMapElement = UtilXml.addChildElement(fieldElement, "key-map", document);
				keyMapElement.setAttribute("field-name", field.get("key-map-" + fkName + "-field-name") + "");
				String relFieldName = field.get("key-map-" + fkName + "-rel-field-name") + "";
				if (!"_NONE_".equals(relFieldName)) {
					keyMapElement.setAttribute("rel-field-name", relFieldName);
				}
			}
		}
		UtilXml.writeXmlDocument(file.getCanonicalPath(), document);

		file = getEntityModelGroupXMLFile(parameters);
		document = UtilXml.readXmlDocument(file.toURI().toURL(),false);
		Element entityGroup = UtilXml.addChildElement(document.getDocumentElement(), "entity-group", document);
		entityGroup.setAttribute("entity", entityName);
		entityGroup.setAttribute("group", getDataSourceName(parameters));
		UtilXml.writeXmlDocument(file.getCanonicalPath(), document);
		EntityEngineUtil.reInitDelegator((String) parameters.get("dataSourceName"));

		return UtilMisc.toMap();
	}

	public static Map<String, Object> updateEntityModel(Map<String, Object> parameters)
			throws Exception {
		parameters.put("new-entity-name", (String) parameters.get("entityName"));
		deleteEntityModel(parameters);
		Map<String, Object> stringObjectMap = addEntityModel(parameters);
		return stringObjectMap;

	}

	public static Map<String, Object> deleteEntityModel(Map<String, Object> parameters)
			throws SAXException, ParserConfigurationException, IOException {
		String entityName = (String) parameters.get("entityName");
		File file = getEntityModelXMLFile(parameters);
		Document document = UtilXml.readXmlDocument(file.toURI().toURL(),false);
		Element element = UtilXml.firstChildElement(document.getDocumentElement(), "entity", "entity-name", entityName);
		document.getDocumentElement().removeChild(element);
		UtilXml.writeXmlDocument(file.getCanonicalPath(), document);

		file = getEntityModelGroupXMLFile(parameters);
		document = UtilXml.readXmlDocument(file.toURI().toURL(),false);

		element = UtilXml.firstChildElement(document.getDocumentElement(), "entity-group", "entity", entityName);
		document.getDocumentElement().removeChild(element);
		UtilXml.writeXmlDocument(file.getCanonicalPath(), document);
		return UtilMisc.toMap();
	}

	public static String deleteEntityModel(HttpServletRequest req, HttpServletResponse resp)
			throws SAXException, ParserConfigurationException, IOException {

		Map<String, Object> parameterMap = UtilHttp.getParameterMap(req);
		deleteEntityModel(parameterMap);
		return "success";
	}

	private static File getEntityModelXMLFile(Map<String, Object> parameters) throws MalformedURLException {
		return new File(ComponentLocationResolver.getBaseLocation(getEntityFilePrefix(parameters) + ".xml").toString());
	}

	private static File getEntityModelGroupXMLFile(Map<String, Object> parameters) throws MalformedURLException {
		return new File(
				ComponentLocationResolver.getBaseLocation(getEntityFilePrefix(parameters) + "-group.xml").toString());
	}

	private static String getEntityFilePrefix(Map<String, Object> parameters) throws MalformedURLException {
		String entityDefForder = (String) parameters.get("entityDefForder");
		if (UtilValidate.isEmpty(entityDefForder)) {
			entityDefForder = "component://entityMgr/entitydef/";
		}
		return entityDefForder + getDataSourceName(parameters);
	}

	private static String getDataSourceName(Map<String, Object> parameters) {
		String dataSourceName = (String) parameters.get("entityModelXMLName");
		if (dataSourceName == null) {
			dataSourceName = parameters.get("dataSourceName") + "";
		}
		return dataSourceName;
	}

}
