import org.bson.Document;
import org.bson.types.ObjectId;

import com.hanlin.fadp.petrescence.datasource.MongoService;
parameters=(Map)parameters
def result=MongoService.getCollection(parameters.mongoCollectionName).deleteOne(new Document("_id",new ObjectId(parameters._id)))
println result
if(result.deletedCount>0){
	return "success"
}
return "error"