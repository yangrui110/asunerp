import org.bson.Document;
import org.bson.types.ObjectId;

import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.petrescence.datasource.MongoService;

def dimFields=['dimField', 'dimFieldType', 'dimFieldDescription']
def attrFields=[
	'attrField',
	'attrFieldType',
	'attrFieldDescription'
]
def fieldInfo=MongoService.findOne(parameters.mongoCollectionName, new Document("_id",MongoService._fieldInfo))
if(parameters._id){
	mData=MongoService.findOne(parameters.mongoCollectionName, new Document("_id",new ObjectId(parameters._id)))
}else{
	mData=[:]
}

def list=[]
context.list=list
fieldInfo.fieldList.each {field->
	if(field.containsKey("dimField")){
		list<<UtilMisc.toMap("name",field["dimField"],"type",field["dimFieldType"],"value",mData[field["dimField"]])
	}else{
		list<<UtilMisc.toMap("name",field["attrField"],"type",field["attrFieldType"],"value",mData[field["attrField"]])
	}
}