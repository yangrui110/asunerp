import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.Document;

import com.hanlin.fadp.base.util.StringUtil;
import com.hanlin.fadp.petrescence.datasource.MongoService;
import com.mongodb.client.FindIterable;

def parameters=(Map)parameters
def request	=(HttpServletRequest)request
def response=(HttpServletResponse)response

def condition=new Document()
def collection=MongoService.getCollection(parameters["mongoCollectionName"])
def fieldInfo=MongoService.findOne(parameters["mongoCollectionName"],new Document("_id",MongoService._fieldInfo))
def head=[]
if(fieldInfo&&fieldInfo["fieldList"]){
	fieldInfo["fieldList"].each {fieldObj->
		if(fieldObj.containsKey("dimField")){
			head<<fieldObj["dimField"]
			if(parameters[fieldObj["dimField"]]){
				condition.append(fieldObj["dimField"],parameters[fieldObj["dimField"]])
			}
		}else if(fieldObj.containsKey("attrField")){
			head<<fieldObj["attrField"]
			if(parameters[fieldObj["attrField"]]){
				condition.append(fieldObj["attrField"],java.util.regex.Pattern.compile(parameters[fieldObj["attrField"]]))
			}
		}
	}
}
response.setContentType("text/comma-separated-values; charset=GBK");// 设置response内容的类型
response.setHeader(
		"Content-disposition",
		"attachment;filename="+ URLEncoder.encode(parameters["mongoCollectionName"]+".csv", "UTF-8"));// 设置头部信息
response.getWriter().println StringUtil.join(head, ",")
collection.find(condition).iterator().each {data->
	if(data["_id"]!=MongoService._fieldInfo){
		def temp="";
		head.each {fieldName->
		if(temp!=""){
			temp+=","
		}
		temp+=data[fieldName]
		}
		response.getWriter().println temp
	}
}

response.flushBuffer()