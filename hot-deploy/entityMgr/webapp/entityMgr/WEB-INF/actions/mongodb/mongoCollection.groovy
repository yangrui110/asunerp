import org.bson.Document;

import com.hanlin.fadp.petrescence.datasource.MongoService;
def parameters=(Map)parameters
if(!parameters["mongoCollectionName"]){
	return 
}
def fieldInfo=MongoService.findOne(parameters["mongoCollectionName"], new Document("_id",MongoService._fieldInfo))
if(!fieldInfo||!fieldInfo["fieldList"]){
	fieldInfo=new Document();
	fieldInfo.put('_id',MongoService._fieldInfo)
	fieldList=[]
	field=new Document();
	field.put("dimField","公司")
	fieldList.add(field)
	fieldInfo.put('fieldList',fieldList)
	MongoService.getCollection(parameters["mongoCollectionName"]).replaceOne(new Document('_id',MongoService._fieldInfo), fieldInfo)
	return 
}
def dimFields=['dimField', 'dimFieldType', 'dimFieldDescription']
def attrField=['attrField', 'attrFieldType', 'attrFieldDescription']
dimFields.each {
	parameters[it]=[]
}
attrField.each {
	parameters[it]=[]
}
parameters["description"]=fieldInfo.description
fieldInfo["fieldList"].each {fieldObj->
	def temp=[]
	if(fieldObj.containsKey("dimField")){
		temp.addAll(dimFields)
	}else{
		temp.addAll(attrField)
	}
	fieldObj.each {k,v->
		if(temp.indexOf(k)>-1){
			temp.remove(temp.indexOf(k))
		}
		parameters[k]<<v
	}
	temp.each {nullField->
		parameters[nullField]<<""
	}
}
