import com.hanlin.fadp.entity.transaction.*;
import com.hanlin.fadp.entity.*;
import java.util.*;
import  java.io.*;
import  java.net.*;
import  java.sql.*;
import com.hanlin.fadp.base.util.*;
import com.hanlin.fadp.entity.*;
import com.hanlin.fadp.entity.model.*; 
import com.hanlin.fadp.entity.datasource.*;

      helperName=parameters.helperName;
	  groupName=parameters.group;
	  println helperName+'======='+groupName+"------------";
      StringBuffer sb=new StringBuffer();
      sb.append('<?xml version="1.0" encoding="UTF-8" ?>');
      sb.append("\n	");	
      Collection messages = new LinkedList();
      GenericDAO dao = GenericDAO.getGenericDAO(new GenericHelperInfo(null, helperName));
      List<ModelEntity> newEntList = dao.induceModelFromDb(messages);
       	String title = "Entity of an Apache Open For Business Project (Apache OFBiz) Component";
        String description = "None";
        String copyright = "Copyright 2015 The Apache Software Foundation";
        String author = "None";
        String version = "1.0";
		
		Delegator idelegator;
		
		Delegator localhost_oracle_root = null;
		Delegator localhost_mysql_root = null;
		
		try {
			localhost_mysql_root = DelegatorFactory.getDelegator("localhost_mysql_root");
		} catch (Exception e) {
			println "==============================================================";
			println "Could not find a delegator with the name localhost_mysql_root";
			println "==============================================================";
			localhost_mysql_root = null;
		}
		
		try {
			localhost_oracle_root = DelegatorFactory.getDelegator("localhost_oracle_root");
		} catch (Exception e) {
			println "==============================================================";
			println "Could not find a delegator with the name localhost_oracle_root";
			println "==============================================================";
			localhost_oracle_root = null;
		}
		
		
		
		def mysql_entityGroups = null;
		def oracle_entityGroups = null;
		def entityGroups = null;
		
		if(localhost_mysql_root){
			mysql_entityGroups=localhost_mysql_root.getModelGroupReader().getGroupNames(localhost_mysql_root.getDelegatorBaseName());
		}
		if(localhost_oracle_root){
			oracle_entityGroups = localhost_oracle_root.getModelGroupReader().getGroupNames(localhost_oracle_root.getDelegatorBaseName());
		}
		if(delegator){
			entityGroups = delegator.getModelGroupReader().getGroupNames(delegator.getDelegatorBaseName());
		}
		
		if(entityGroups.contains(groupName) && entityGroups)
		{
			idelegator=delegator;
		}else if(mysql_entityGroups.contains(groupName) && mysql_entityGroups){
			idelegator=localhost_mysql_root;
		}else if(oracle_entityGroups.contains(groupName) && oracle_entityGroups){
			idelegator=localhost_oracle_root;
		}

			//获得表结构
	       ModelReader reader = idelegator.getModelReader();
			Map<String, ModelEntity> map = reader.getEntityCache();
			Collection<ModelEntity> entities = map.values();
			Iterator ecIter = newEntList.iterator();
			List<String> listName=new ArrayList<String>();
			for(ModelEntity me:newEntList)
			{
				listName.add(me.getEntityName());
			}
			sb.append('<entitymodel>\n		<title>'+title+'</title>\n		<description>'+description+'</description>\n		<copyright>'+copyright+'</copyright>\n		<author>'+author+'</author>\n		<version>version</version>\n			');
			  while(ecIter.hasNext()) {
				    ModelEntity entity = (ModelEntity) ecIter.next();
				     if(listName.indexOf(entity.getEntityName())<0) continue;
				      sb.append('<entity entity-name="'+entity.getEntityName()+'" ');
				      if(!entity.getEntityName().equals(ModelUtil.dbNameToClassName(entity.getPlainTableName())) || !ModelUtil.javaNameToDbName(entity.getEntityName()).equals(entity.getPlainTableName()) )
				      {
				      	sb.append('table-name="'+entity.getPlainTableName()+'" ');
				      }
				      sb.append('package-name="'+entity.getPackageName()+'" ');
				     if(entity.getDependentOn().length() > 0)
				     {
				     	sb.append('dependent-on="'+entity.getDependentOn()+'" ');
				     }									
				     if(!title.equals(entity.getTitle()))
				     {
				     	sb.append('title="'+entity.getTitle()+'" ');
				     }
				     if(!copyright.equals(entity.getCopyright()))
				     {
				     	 sb.append('copyright="'+entity.getCopyright()+'" ');
				     }
				      if(!author.equals(entity.getAuthor()))
				     {
				     	 sb.append('author="'+entity.getAuthor()+'" ');
				     }	
				      if(!version.equals(entity.getVersion()))
				     {
				     	 sb.append('version="'+entity.getVersion()+'" ');
				     }	
				     sb.append('>');
				     sb.append("\n				");
				     sb.append('<description>'+entity.getDescription()+'</description>');
				      sb.append("\n				");
				     Iterator<ModelField> fieldIterator = entity.getFieldsIterator();
				      while (fieldIterator.hasNext()) {
								ModelField field = fieldIterator.next();	
								if(field.getName()!='lastUpdatedStamp'&&field.getName()!='lastUpdatedTxStamp'&&field.getName()!='createdStamp'&&field.getName()!='createdTxStamp'){
									sb.append('<field name="'+field.getName()+'" ');
									if(!field.getColName().equals(ModelUtil.javaNameToDbName(field.getName()))){
										sb.append('col-name="'+field.getColName()+'" ');
										}
										sb.append('type="'+field.getType()+'" ');
										for (int v = 0; v<field.getValidatorsSize(); v++) {
											 String valName = (String) field.getValidator(v);
											 sb.append('<validate name="'+valName+'" ');
									   }
									   sb.append('>');
										 sb.append("\n				");
									   sb.append('</field>');
										 sb.append("\n				");
								}
								
							  }
							Iterator<ModelField> pkIterator = entity.getPksIterator();
								while (pkIterator.hasNext()) {
									ModelField field = pkIterator.next();
									sb.append('<prim-key field="'+field.getName()+'" />');
									  sb.append("\n				");
								}
                         if (entity.getRelationsSize() > 0) {
								for (int r = 0; r < entity.getRelationsSize(); r++) {
								  ModelRelation relation = entity.getRelation(r);
								  sb.append('<relation type="'+relation.getType()+'" ');
								  if(relation.getTitle().length() > 0){sb.append('title="'+relation.getTitle()+'" ');}
								  sb.append('rel-entity-name="'+relation.getRelEntityName()+'" >');
								    sb.append("\n				");
									for(int km=0; km<relation.getKeyMapsSize(); km++){
										ModelKeyMap keyMap = relation.getKeyMap(km);
										sb.append('	<key-map field-name="'+keyMap.getFieldName()+'" ');
										if(!keyMap.getFieldName().equals(keyMap.getRelFieldName())){sb.append('rel-field-name="'+keyMap.getRelFieldName()+'" ');}	
										sb.append('/>');
										  sb.append("\n				");
									}
									sb.append('</relation>');
									  sb.append("\n				");
								  }
								 }
				    sb.append('</entity>');	
				      sb.append("\n		");																				 
				    }
			sb.append('</entitymodel>');
			  sb.append("\n");
response.setHeader("Content-type", "text/xml;charset=UTF-8");
response.setCharacterEncoding("UTF-8");
response.setHeader("Content-Disposition", "attachment;filename="+helperName+".xml");
outputStream=response.getOutputStream();
PrintWriter printwriter = new PrintWriter(outputStream);
printwriter.write(sb.toString(), 0, sb.toString().length());
printwriter.close();
