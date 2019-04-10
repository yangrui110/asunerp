import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.types.ObjectId;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.petrescence.datasource.MongoService;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.UpdateOptions;
def parameters=(Map)parameters
def request	=(HttpServletRequest)request
def response=(HttpServletResponse)response
def formData=request.getParameterMap()
println "saveMonoData.groovy 14 提交的数据"+formData
if(request.getMethod().equalsIgnoreCase("post")){
	def fieldInfo=MongoService.findOne(parameters["mongoCollectionName"],new Document("_id",MongoService._fieldInfo))
	def newData=new Document()
	fieldInfo.fieldList.each {
		fieldName=it.dimField?it.dimField:it.attrField
		if(parameters[fieldName]){
			newData.append(fieldName, parameters[fieldName])
		}
	}
	println newData
	def b=false
	if(parameters._id){
		result=MongoService.updataMany(parameters["mongoCollectionName"], new Document("_id",new ObjectId(parameters._id)), new Document("\$set",newData))
		if(result.modifiedCount==1){
			b=true
		}
	}else{
		MongoService.insertMany(parameters["mongoCollectionName"], [newData])
		if(newData.get("_id")){
			b=true
		}
	}
	if(b){
		response.getWriter().print("success")
		return "success"
	}else{
		return "error"
	}
	
}else{
	return "get"
}