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
import net.sf.json.JSONObject;
import net.sf.json.*;
import com.hanlin.fadp.entity.condition.*;
def jsonData = parameters._json;
println jsonData+"----------------"
JSONObject json = null;
if(jsonData){
	json = JSONObject.fromObject(jsonData);
	println json;
}


println "************************";
println parameters.record;
println parameters.entityName;
println request.getParameter("entityName");
println "*************************";

def dataSourceName=parameters.dataSourceName;
println dataSourceName
def entityName = parameters.entityName;

Delegator idelegator;

	idelegator = DelegatorFactory.getDelegator("localhost_mysql_root");


def modelEntity = idelegator.getModelEntity(entityName);
def tempFieldList = [];
def fieldList = [];
def fieldsType = [:];
fieldIterator = modelEntity.getFieldsIterator();

while (fieldIterator.hasNext()) {
	ModelField field = fieldIterator.next();
	ModelFieldType type = idelegator.getEntityFieldType(idelegator.getModelEntity(entityName), field.getType());
	tempFieldList.add(field.getName());
	fieldsType.put(field.getName(), type.getJavaType());
}


for(def i=0; i<tempFieldList.size(); i++){
	if(tempFieldList[i]!='lastUpdatedStamp'&&tempFieldList[i]!='lastUpdatedTxStamp'&&tempFieldList[i]!='createdStamp'&&tempFieldList[i]!='createdTxStamp')
	{
	 fieldList.add(tempFieldList[i]);
	}
}



context.fieldList = fieldList;




def form_condition = parameters.form_data;
def conditionList = [];
def condition = null;
def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

if(jsonData){
	
	for(def i=0; i<json.size(); i++){ 
		println json.get(""+i);
		k = json.get(""+i).split("#");
		println k;
		println "==============="
		if(k.size() > 2){
			def t = getTypeData(fieldsType.get(k[0]), k[2], sdf);
			if(k[1] == "like"){
				conditionExpr= EntityCondition.makeCondition(k[0],getOperator(k[1]),"%"+t+"%");
			}else{
				conditionExpr= EntityCondition.makeCondition(k[0],getOperator(k[1]),t);
			}
			
			conditionList.add(conditionExpr)
		}
	}
	
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	
}





def pageSize = 10;
def requestPage = 1;

if(parameters.pageSize){
	pageSize = parameters.pageSize.toInteger();
}
if(parameters.requestPage){
	requestPage = parameters.requestPage.toInteger();
}

def lowIndex = (requestPage - 1)*pageSize + 1;


efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
def eli = idelegator.find(entityName, condition, null, null, null, efo);
def totalRow=0;
def totalPages=0;
def record=0;
if(eli!=null)
{
    totalRow = eli.getResultsSizeAfterPartialList();
	 totalPages = (int)(totalRow / pageSize) + (totalRow % pageSize == 0 ? 0 : 1);
	
	 record= eli.getPartialList(lowIndex,pageSize);
}
	eli.close();


context.record = record;
context.totalRow = totalRow;
context.totalPages = totalPages;
context.currPage = requestPage;
context.parameters.pageSize = pageSize;
context.entityName = entityName;
context.fieldsType = fieldsType;



def getOperator(op){
	switch(op){
		case "=" : return EntityOperator.EQUALS
		case ">" : return EntityOperator.GREATER_THAN
		case ">=" : return EntityOperator.GREATER_THAN_EQUAL_TO
		case "<" : return EntityOperator.LESS_THAN
		case "<=" : return EntityOperator.LESS_THAN_EQUAL_TO
		case "like": return EntityOperator.LIKE
		default :return EntityOperator.EQUALS
	}
}

def getTypeData(type, data){
	switch(type){
		case "Double" : return data.toInteger();
		case "Long" : return data.Integer();
		case "java.math.BigDecimal" : return data.toBigDecimal();
		case "java.sql.Date" : time = sdf.parse(data); return new java.sql.Date(time.getTime()); 
		case "java.sql.Time" : time = sdf.parse(data); return new java.sql.Time(time.getTime());
		case "String" : return data;
		case "java.sql.Timestamp" : time = sdf.parse(data); return new java.sql.Timestamp(time.getTime());
	
		default : return data;
	}
}

def findTrue(fieldList, data){
	def i = 0;
	for(i=0; i<fieldList.size(); i++){
		if(fieldList[i] == data){
			break;
		}
	}
	
	if(i < fieldList.size()){
		return true;
	}else{
		return false;
	}
}