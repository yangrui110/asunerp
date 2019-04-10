import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.petrescence.datasource.MongoService;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.UpdateOptions;
def parameters=(Map)parameters
def request	=(HttpServletRequest)request
def response=(HttpServletResponse)response
def formData=request.getParameterMap()
println "saveMonoCollection.groovy 12 提交的数据"+formData
if(request.getMethod().equalsIgnoreCase("post")){

	if(formData["dimField"]){
		if(formData["attrField"]){
			def dimField=['dimField', 'dimFieldType', 'dimFieldDescription']
			def attrField=['attrField', 'attrFieldType', 'attrFieldDescription']
			def fieldInfo=new Document();
			fieldInfo["description"]=parameters["description"]
			def fieldList=[]
			def addBlok={fields,fieldName->
				for (def i=0;i<formData[fieldName].length;i++) {
					def doc=new Document()
					fields.each {k->
						doc[k]=formData[k][i]
					}
					fieldList<<doc
				}
			}
			addBlok(dimField,"dimField")
			addBlok(attrField,"attrField")
			fieldInfo["fieldList"]=fieldList
			collection=MongoService.getCollection(parameters["mongoCollectionName"])
			collection.replaceOne(new Document('_id',MongoService._fieldInfo), fieldInfo,new UpdateOptions().upsert(true))
			UtilMisc.toListArray(formData["dimField"]).each {  
				collection.createIndex(new Document(it,1))
			}
			response.writer.print "success"
		}else{
			response.writer.print "请填写属性!"
		}
	}else{
		response.writer.print "请填写维度!"
	}

	return "success"
}else{
	return "get"
}