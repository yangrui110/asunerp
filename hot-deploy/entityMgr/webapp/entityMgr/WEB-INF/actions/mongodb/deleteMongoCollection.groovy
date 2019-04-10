import com.hanlin.fadp.petrescence.datasource.MongoService;
collection=MongoService.getCollection(parameters.mongoCollectionName)
if(collection){
	collection.drop()
}
return "success"