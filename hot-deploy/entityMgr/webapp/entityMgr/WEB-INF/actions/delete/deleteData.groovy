import com.hanlin.fadp.base.util.*;
import com.hanlin.fadp.entity.*;
import com.hanlin.fadp.entity.model.*;
import com.hanlin.fadp.entity.util.*;
import com.hanlin.fadp.entity.datasource.*;

import javolution.util.FastList;

import java.util.Date;
import java.text.*;

import com.hanlin.fadp.entity.*;

import javax.jms.Session;

import org.eclipse.birt.data.engine.script.ScriptEvalUtil.MiscUtil;

import net.sf.json.*;

import com.hanlin.fadp.entity.condition.*;
import com.hanlin.fadp.entity.transaction.*;


def jsonData = parameters._json;
def groupName = parameters.groupName;
def tableName = parameters.tableName;


JSONObject json = null;
if(jsonData){
	json = JSONObject.fromObject(jsonData);
}
println json;
//由group 到 delegator的转换
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


beganTransaction = TransactionUtil.begin(7200);
try {
	def map = [:];
	println json.size();
	println json.get("0");
	println json.get("1");
	for(def i=0; i<json.size(); i++){
		def data = json.get(""+i);
		println data;
		map = reserveMap(JSONObject.fromObject(data));
		println map;
		if(map != null){
			idelegator.removeByAnd(tableName, map);
		}else{
			println "Error: Could Not To Delete Data";
		}
		
	}
	TransactionUtil.commit(beganTransaction);
	request.setAttribute("resultState", "success");
	return "success";
} catch (Exception e) {
	e.printStackTrace();
	errMsg = "Error delete [" + groupName + ": " + tableName + "]" + " data!";
	request.setAttribute("resultState", "error");
	TransactionUtil.rollback(beganTransaction, errMsg, e);
}

def reserveMap(data){
	def map = [:];
	data.each{k, v->
		if(v){
			map.put(k, v);
		}
	}
	return map;
}

context.groupName = groupName;
context.tableName = tableName;

println "*********************";
println parameters.groupName;
println parameters.tableName;
println "**********************";
