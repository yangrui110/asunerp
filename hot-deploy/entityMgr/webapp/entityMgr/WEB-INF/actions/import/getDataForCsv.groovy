//实现自动提示功能

import com.hanlin.fadp.base.util.*;
import com.hanlin.fadp.entity.*;
import com.hanlin.fadp.entity.model.*;
import com.hanlin.fadp.entity.util.*;
import com.hanlin.fadp.entity.datasource.*;

import javolution.util.FastList;

import java.util.Date;
import java.io.ObjectOutputStream.DebugTraceInfoStack;
import java.text.*;

import com.hanlin.fadp.entity.*;

import javax.jms.Session;

import net.sf.json.*;

import com.hanlin.fadp.entity.condition.*;
def tempTableName = parameters.value;

def groupName = parameters.groupName;
def entityResults = [];

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

def entityResults_first = [];

entityList = idelegator.getModelEntityMapByGroup(groupName);
entityList.each{ k, v->
//	println "===================" + k;
		Debug.log("load database's entity name ...");
		entityResults.add(k);
//		entityResults_first。add(k);
}
request.setAttribute("jsonName", entityResults);
context.entityResults_first = entityResults;
return "success";