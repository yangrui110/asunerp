package com.hanlin.fadp.petrescence.datasource;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hanlin.fadp.base.location.ComponentLocationResolver;
import com.hanlin.fadp.base.util.FileUtil;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.model.ModelEntity;

public class FindMissedEntity {
	public static Set<String> entitySet=new HashSet<>();
	public static void addMissed(String entity){
		entitySet.add(entity);
	}
	public static void writeXML(Set<String> set){
		if(set==null){
			set=entitySet;
		}
		try{
			File entityModelFile = new File(ComponentLocationResolver.getBaseLocation( DataSourceWorker.entityModelXml_pre  + "entitymodel_localsys.xml").toString());
			File entityGroupFile = new File(ComponentLocationResolver.getBaseLocation( DataSourceWorker.entityModelXml_pre  + "localsys-group.xml").toString());
			if(!entityModelFile.exists()){
				Files.copy(FileUtil.getFile(DataSourceWorker.entityModelXml_pre + "template.xml").toPath(), entityModelFile.toPath());
			}
			Document entityModelDocument = UtilXml.readXmlDocument(entityModelFile.toURI().toURL());
			Document entityGroupDocument = UtilXml.readXmlDocument(entityGroupFile.toURI().toURL());
			for(Element ele:UtilXml.childElementList(entityModelDocument.getDocumentElement())){
				entityModelDocument.getDocumentElement().removeChild(ele);
			}
			for(Element ele:UtilXml.childElementList(entityGroupDocument.getDocumentElement())){
				entityGroupDocument.getDocumentElement().removeChild(ele);
			}
			Delegator delegator=DelegatorFactory.getDelegator("default");
			for (String modelEntityName : set) {
				try{
					
					ModelEntity modelEntity = delegator.getModelEntity(modelEntityName);
					Element xmlElement = modelEntity.toXmlElement(entityModelDocument);
					for(Element ele:UtilXml.childElementList(xmlElement,"relation","type","many")){
						xmlElement.removeChild(ele);
					}
					entityModelDocument.getDocumentElement().appendChild(xmlElement);
					Element entityGroup = UtilXml.addChildElement(entityGroupDocument.getDocumentElement(), "entity-group", entityGroupDocument);
					entityGroup.setAttribute("entity", modelEntityName);
					entityGroup.setAttribute("group", "com.hanlin.fadp");
				}catch(Exception e2){
					System.out.println(modelEntityName);
				}
			}
			UtilXml.writeXmlDocument(entityModelFile.getCanonicalPath(), entityModelDocument);
			UtilXml.writeXmlDocument(entityGroupFile.getCanonicalPath(), entityGroupDocument);
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}
}
