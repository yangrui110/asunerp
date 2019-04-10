import org.bson.Document;

import com.hanlin.fadp.petrescence.datasource.MongoService;
import com.mongodb.client.FindIterable;
def list=[]
context.list=list
MongoService.getDatabase().listCollectionNames().each{
	if(!it.startsWith("system.")){
		def collection=MongoService.getCollection(it)
		def docIt=collection.find(new Document("_id","_collectionInfo")).iterator()
		def map=["mongoCollectionName":it]
		list<<map
		if(docIt.hasNext()){
			collectionInfo=docIt.next()
			map["mongoCollectionDescription"]=collectionInfo.get("description");
		}else{
			map["mongoCollectionDescription"]="";
		}
	}
}
