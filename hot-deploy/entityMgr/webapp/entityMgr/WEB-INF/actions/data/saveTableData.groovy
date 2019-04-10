import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.entity.DelegatorFactory
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.petrescence.datasource.BaseInfo;
import com.hanlin.fadp.petrescence.datasource.EntityModelWorker;
def parameters=(Map<String,Object>)parameters
def myDelegator=DelegatorFactory.getDelegator(parameters.dataSourceName)
def request	=(HttpServletRequest)request
def response=(HttpServletResponse)response
response.setContentType("application/json")
if(request.method.equalsIgnoreCase("get")){
	return "get"
}
def pkValue=myDelegator.makeValue(parameters.entityName)
def hasPk=false;
parameters.each{k,v->
	if(k.startsWith("pk_")){
		hasPk=true;
		pkValue.setString(k.substring(3), v)
	}
}
/*if(!hasPk){
	response.writer.print """{"error":"没有主键"}"""
	return "success"
}
*/
def error;
if(request.getMethod().equalsIgnoreCase("post")){
	try{
		def entity=myDelegator.getModelEntity(parameters.entityName)
		entity.setNoAutoStamp(true)
		def fieldSet=BaseInfo.getFieldSetWidthOutTimeStamp(entity)
		def value=myDelegator.makeValue(parameters.entityName)
		fieldSet.each {
			if(parameters[it]){
				value.setString(it, parameters[it])
			}
		}
		
//		value=myDelegator.makeValidValue(parameters.entityName, parameters)
		if(!hasPk){
			newValue=myDelegator.create(value)
		}else{
			newValue=myDelegator.storeByCondition(parameters.entityName,value,EntityCondition.makeCondition(pkValue))
		}
		logInfo "保存成功"+newValue
		if(!newValue){
			error="保存失败"
		}
	}catch(e){
		e.printStackTrace()
		error=e.getMessage()
	}
	if(error){
		response.writer.print "{\"error\":\"${error}\"}"
	}else{
		response.writer.print "{}"
	}
	return "success"

}else{
	return "get"
}
