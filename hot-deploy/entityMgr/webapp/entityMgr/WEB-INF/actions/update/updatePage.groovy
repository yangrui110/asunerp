
import com.hanlin.fadp.entity.condition.EntityCondition;
import javax.jms.Session;
import com.hanlin.fadp.entity.*;
import java.util.*;
import  java.io.*;
import  java.net.*;
import  java.sql.*;
import com.hanlin.fadp.base.util.*;
import com.hanlin.fadp.entity.*;
import com.hanlin.fadp.entity.model.*;
import com.hanlin.fadp.entity.datasource.*;
import com.hanlin.fadp.entity.util.EntityFindOptions;

//用的是ajax的提交方式，不用额外传参
def groupName = parameters.databaseName;
def tableName = parameters.tableName;


def temp = [];



//对页数的处理
def pageSize = 10;
def requestPage = 1;

if(parameters.pageSize){
	pageSize = parameters.pageSize.toInteger();
}
if(parameters.requestPage){
	requestPage = parameters.requestPage.toInteger();
}

def lowIndex = (requestPage - 1)*pageSize + 1;

//解决由group到delegator的逆向处理
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


//搞到表结构
def modelEntity = idelegator.getModelEntity(tableName);
def tempFieldList = [];
def fieldList = [];
def fieldsType = [:];
def pkField = [];
def tempNonePk = [];
def nonePk = [];

fieldIterator = modelEntity.getFieldsIterator();

while (fieldIterator.hasNext()) {
	ModelField field = fieldIterator.next();
	ModelFieldType type = idelegator.getEntityFieldType(idelegator.getModelEntity(tableName), field.getType());
	
	if(field.isPk){
		pkField.add(field.getName());
	}else{
		tempNonePk.add(field.getName());
	}
	tempFieldList.add(field.getName());
	fieldsType.put(field.getName(), type.getJavaType());
}

println "============";
println pkField;
println pkField.size();
println tempFieldList;
println "=============";

for(def i=0; i<tempFieldList.size(); i++){
	if(tempFieldList[i]!='lastUpdatedStamp'&&tempFieldList[i]!='lastUpdatedTxStamp'&&tempFieldList[i]!='createdStamp'&&tempFieldList[i]!='createdTxStamp')
	{
		fieldList.add(tempFieldList[i]);
	}
}

for(def i=0; i<tempNonePk.size(); i++){
	if(tempNonePk[i]!='lastUpdatedStamp'&&tempNonePk[i]!='lastUpdatedTxStamp'&&tempNonePk[i]!='createdStamp'&&tempNonePk[i]!='createdTxStamp')
	{
		nonePk.add(tempNonePk[i]);
	}
}


efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
def eli = idelegator.find(tableName, null, null, null, null, efo);
def totalRow=0;
def totalPages=0;
def record=null;
if(eli!=null)
{
	totalRow = eli.getResultsSizeAfterPartialList();
	 totalPages = (int)(totalRow / pageSize) + (totalRow % pageSize == 0 ? 0 : 1);
	
	 record= eli.getPartialList(lowIndex,pageSize);
}
eli.close();



//以html的形式显示
context.record = record;
context.lowIndex = lowIndex;
context.parameters.pageSize = pageSize;
context.currPage = requestPage;
context.totalRow = totalRow;
context.totalPages = totalPages;
context.groupName = groupName;
context.fieldList = fieldList;
context.tableName = tableName;
context.fieldsType = fieldsType;
context.pkField = pkField;
context.nonePk = nonePk;




println "**************************************";
println groupName;
println tableName;
println record;
println fieldList;
println "**************************************";
	

return "success";