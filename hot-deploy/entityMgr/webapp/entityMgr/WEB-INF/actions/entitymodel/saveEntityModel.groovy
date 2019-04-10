import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.petrescence.datasource.EntityModelWorker

import groovy.json.JsonOutput;;
def parameters=(Map)parameters


if(request.getMethod().equalsIgnoreCase("post")){
	def request	=(HttpServletRequest)request
	def response=(HttpServletResponse)response
	response.setContentType("application/json")
	//println "fuck99999"
	try{
		if(parameters["entityName"]){
			EntityModelWorker.updateEntityModel(parameters)
		}else{
			EntityModelWorker.addEntityModel(parameters)
		}
		response.writer.print(JsonOutput.toJson([]))
	}catch(e){
	e.printStackTrace();
		response.writer.print(JsonOutput.toJson([errorMsg:e.getMessage()+"."]))
	}
	return "success"
}else{
	return "get"
}
