import net.sf.json.JSONObject;
import net.sf.json.*;
import com.hanlin.fadp.entity.*;
import com.hanlin.fadp.entity.model.*;
import java.text.SimpleDateFormat;
import com.hanlin.fadp.entity.transaction.*;


def jsonData = parameters._json;
def groupName = parameters.groupName;
def tableName = parameters.tableName;
def oldData = parameters.oldData;
def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//被修改的value
def value = null;

JSONObject json = null;
if(jsonData){
	json = JSONObject.fromObject(jsonData);
}

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


//搞到表结构
def modelEntity = idelegator.getModelEntity(tableName);
def fieldsType = [:];

fieldIterator = modelEntity.getFieldsIterator();

while (fieldIterator.hasNext()) {
	ModelField field = fieldIterator.next();
	
	ModelFieldType type = idelegator.getEntityFieldType(idelegator.getModelEntity(tableName), field.getType());
	fieldsType.put(field.getName(), type.getJavaType());
}

// findOne


if(json){
	def map = [:];
	map = reserveMap(json, fieldsType, sdf);	
	if(map.size() > 0){
		beganTransaction = TransactionUtil.begin(3600);
		
		try {
				value = idelegator.makeValue(tableName, map);
				idelegator.store(value);
				request.setAttribute("result", "success");
				TransactionUtil.commit(beganTransaction);
				return "success";
		} catch (Exception e) {
			e.printStackTrace();
			errMsg = "Error add [" + groupName + ": " + tableName + "]" + " data!";
			request.setAttribute("result", "error");
			TransactionUtil.rollback(beganTransaction, errMsg, e);
			return "error";
		}
	}
	
}

def getTypeData(type, data, sdf){
	switch(type){
		case "Double" : return data.toDouble();
		case "Long" : return (Long)data.toInteger();
		case "java.math.BigDecimal" : return data.toBigDecimal();
		
		case "java.sql.Date" : time = sdf.parse(data); return new java.sql.Date(time.getTime());
		case "java.sql.Time" : time = sdf.parse(data); return new java.sql.Time(time.getTime());
		case "String" : return data;
		
		case "java.sql.Timestamp" : time = sdf.parse(data); return new java.sql.Timestamp(time.getTime());
		//还没有对int进行修改
		default : return data;
	}
}

//modefy 
def reserveMap(data, fieldsType, sdf){
	def map = [:];
	data.each{k, v->
		if(v){
			getTypeData(fieldsType.get(data[0]), data[1], sdf)
			map.put(k, getTypeData(fieldsType.get(k), v, sdf));
		}
	}
	if(map == null){
		return null;
	}
	return map;
}


println "************************";
println json;
println groupName;
println tableName;
println oldData;
println "************************";