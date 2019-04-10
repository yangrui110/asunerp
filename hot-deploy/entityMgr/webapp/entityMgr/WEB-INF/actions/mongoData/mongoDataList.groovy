import org.bson.Document;

import com.hanlin.fadp.base.util.StringUtil
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.petrescence.datasource.MongoService;
import com.mongodb.client.FindIterable;
def parameters=(Map)parameters

def dimCondition=[]
def attrCondition=[]
def columns=[]
context.dimCondition=dimCondition
context.attrCondition=attrCondition
def condition=new Document()
def collection=MongoService.getCollection(parameters["mongoCollectionName"])
def fieldInfo=MongoService.findOne(parameters["mongoCollectionName"],new Document("_id",MongoService._fieldInfo))
println 'fieldInfo["fieldList"]'+fieldInfo["fieldList"]
condition.append("_id",new Document("\$ne",MongoService._fieldInfo))
if(fieldInfo&&fieldInfo["fieldList"]){
	for(fieldObj in fieldInfo["fieldList"]){
//	fieldInfo["fieldList"].each {fieldObj->
		def temp=[:]
		if(fieldObj.containsKey("dimField")){
			if(parameters[fieldObj["dimField"]]){
				condition.append(fieldObj["dimField"],parameters[fieldObj["dimField"]])
			}
			temp["dimField"]=StringUtil.wrapString(fieldObj["dimField"])
			temp["options"]=[]
			try{
				collection.distinct(fieldObj["dimField"], String.class)
				.filter(new Document(fieldObj["dimField"],new Document("\$ne",null)))
				.iterator().each {
					temp["options"]<<StringUtil.wrapString(it)
				}
			}catch(e){
				e.printStackTrace()
			}
			dimCondition<<temp
			columns<<fieldObj["dimField"]
		}else{
			if(parameters[fieldObj["attrField"]]){
				condition.append(fieldObj["attrField"],java.util.regex.Pattern.compile(parameters[fieldObj["attrField"]]))
			}
			temp["attrField"]=StringUtil.wrapString(fieldObj["attrField"])
			attrCondition<<temp
			columns<<fieldObj["attrField"]
		}
	}
}
def list=[]

context.list=list
println "mongoDataList.groovy 43: condition:${condition}"

//====分页代码1=====
println "pagesize:"+parameters.pageSize
if(parameters.pageSize==null){
	parameters.pageSize=10
}else if(parameters.pageSize instanceof String ){
	parameters.pageSize=Integer.parseInt(parameters.pageSize)
}
if(parameters.currPage==null){
	parameters.currPage=1;
}else if(parameters.currPage instanceof String ){
	parameters.currPage=Integer.parseInt(parameters.currPage)
}
//====分页代码1===end==



def records=[]
context.columns=columns
context.records=records
cursor=collection.find(condition).skip(parameters.pageSize*(parameters.currPage-1)).limit(100).iterator()
cursor.each {
//	if(it["_id"]!=MongoService._fieldInfo){
		record=[]
		record<<it["_id"]
		columns.each {fieldName->
			record<<it[fieldName]
		}
		records<<record
//	}
}
cursor.close();
//====分页代码2=====
int i=0
collection.find(condition).each{i++}
context.pageSize=parameters.pageSize!=null?parameters.pageSize:10
context.currPage=parameters.currPage!=null?parameters.currPage:10
context.totalRow=i
context.totalPages=Math.ceil(i/(parameters.pageSize))

delegator=(Delegator)delegator
def pageSizeEntities=delegator.findList("Dictionary",EntityCondition.makeCondition([EntityCondition.makeCondition(["module":"pageSize"])]), null, null, null, false)
def pageSizeList=[]
if(pageSizeEntities){
	pageSizeEntities.each{
		pageSizeList << Integer.parseInt(it.value)
	}
	pageSizeList.sort()
}else{
	pageSizeList=[10,20,50]
}
context.pageSizeList=pageSizeList
//====分页代码2===end==
parameters.each {k,v->
	if(v&&v instanceof String){
		parameters[k]=StringUtil.wrapString(v)
	}
}


