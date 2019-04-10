import com.hanlin.fadp.common.FindServices;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.util.EntityQuery;
import com.hanlin.fadp.petrescence.datasource.BaseInfo;

def parameters=(Map<String,Object>)parameters
def myDelegator=DelegatorFactory.getDelegator(parameters.dataSourceName)
def pkMap=[:]
def entity=myDelegator.getModelEntity(parameters.entityName)
def fieldSet
if(entity.pkFields){
	
	 fieldSet=BaseInfo.getFieldSetWidthOutTimeStamp(entity).toList()
}else{
	fieldSet=entity.allFieldNames
}
def hasPk=false;
parameters.each{k,v->
	if(k.startsWith("pk_")){
		hasPk=true;
		pkMap.put(k.substring(3), v)
	}
}
context.pkMap=pkMap
def value=[:]
if(hasPk){
	def defaultDelegator=delegator
	this.binding.setVariable('delegator',myDelegator);
	def conditionList=FindServices.createConditionList(pkMap,entity.fieldsList,[:],myDelegator,[:])
//	value=myDelegator.findByAnd(parameters.entityName,pkMap,null,false)[0]
	value=(from(parameters.entityName)as EntityQuery).select(fieldSet.toSet()).where(conditionList).queryOne()
}
def list=[]
fieldSet.each {
	list<<[name:it,type:entity.getField(it).getType(),isPk:entity.getField(it).isPk,value:value.get(it)?:""]
}
context.list=list


