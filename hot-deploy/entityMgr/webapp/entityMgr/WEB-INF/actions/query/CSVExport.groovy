
import com.hanlin.fadp.entity.model.ModelField;
import com.hanlin.fadp.entity.*;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelField;
import com.hanlin.fadp.entity.model.ModelFieldType;
import com.hanlin.fadp.entity.GenericDelegator;
import com.hanlin.fadp.entity.util.EntityFindOptions;
import com.hanlin.fadp.entity.condition.EntityExpr;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityConditionList;
import com.hanlin.fadp.entity.condition.EntityOperator;
import javolution.util.FastList;
import java.util.Date;
import java.text.*;



def entityName = parameters.entityName;
def modelEntity = delegator.getModelEntity(entityName);
def tempFieldList = [];
def fieldList = [];
def fieldsType = [:];
fieldIterator = modelEntity.getFieldsIterator();

while (fieldIterator.hasNext()) {
	ModelField field = fieldIterator.next();
	ModelFieldType type = delegator.getEntityFieldType(delegator.getModelEntity(entityName), field.getType());
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

if(form_condition){
	form_condition = form_condition.split("&");
	form_condition.each{ k->
		k = k.split("#");
		println k;
		if(k.size() > 2){
			k[2] = getTypeData(fieldsType.get(k[0]), k[2]);
			
			if(k[1] == "like"){
				conditionExpr= EntityCondition.makeCondition(k[0],getOperator(k[1]),"%"+k[2]+"%");
			}else{
				conditionExpr= EntityCondition.makeCondition(k[0],getOperator(k[1]), k[2]);
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
def eli = delegator.find(entityName, condition, null, null, null, efo);
def totalRow = eli.getResultsSizeAfterPartialList();
def totalPages = (int)(totalRow / pageSize) + (totalRow % pageSize == 0 ? 0 : 1);

def record= eli.getPartialList(lowIndex,pageSize);


eli.close();

context.record = record;
context.totalRow = totalRow;
context.totalPages = totalPages;
context.currPage = requestPage;
context.parameters.pageSize = pageSize;
context.entityName = entityName;
context.fieldsType = fieldsType;

println record;


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
		case "Long" : return (Long) data.Integer();
		case "java.math.BigDecimal" : return data.toBigDecimal();
		case "java.sql.Date" : time = sdf.parse(data); return new java.sql.Date(time.getTime()); 
		case "java.sql.Time" : time = sdf.parse(data); return new java.sql.Time(time.getTime());
		case "String" : return data;
		
		default : return data;
	}
}

def translateType(fieldType, field){
	//按照类型转换
	if(fieldType == "String"){
		return field;
	}else if(fieldType == "Double"){
		return field.toDouble();
	}else if(fieldType == "Long"){
		return (Long)insertValue[k].toDouble();
	}else if(fieldType.indexOf("BigDecimal") >= 0){
		return insertValue[k].toBigDecimal();
	}else if(fieldType.indexOf("Timestamp") >= 0){
		java.util.Date start_temp;
		java.sql.Timestamp start;
		def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		start_temp = sdf.parse(insertValue[k]);
		start = new java.sql.Timestamp(start_temp.getTime());
		return start;

	}else if(fieldType == "java.sql.Date"){
		java.util.Date start_temp;
		def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		start_temp = sdf.parse(insertValue[k]);
		return start_temp;
	}else if(fieldType == "java.sql.Time"){
		java.util.Date start_temp;
		java.sql.Time start;
		def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		start_temp = sdf.parse(insertValue[k]);
		start = new java.sql.Time(start_temp.getTime());
		return start;
	}else{
		//还没有对int进行处理
	}
}

def getDelegator(){
	
	return ;
}

