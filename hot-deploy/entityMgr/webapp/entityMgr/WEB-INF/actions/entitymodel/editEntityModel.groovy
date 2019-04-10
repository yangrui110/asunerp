import net.sf.json.JSONObject;

import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.GenericDelegator;
import com.hanlin.fadp.entity.model.ModelFieldTypeReader;
import com.hanlin.fadp.petrescence.datasource.BaseInfo;
import com.hanlin.fadp.petrescence.datasource.EntityModelWorker;
def dataSourceName=parameters.dataSourceName
delegator=DelegatorFactory.getDelegator(dataSourceName)
def reader=ModelFieldTypeReader.getModelFieldTypeReader(dataSourceName)
context.entityNameList=EntityModelWorker.readEntityModelListFromXML(parameters)
context.fieldTypeList=reader.fieldTypeNames.toList();
def json=["fieldTypeListJson":reader.fieldTypeNames.toList()]
	json["entityNamesJson"]=context.entityNameList
if(parameters['entityName']){
	map=EntityModelWorker.readEntityModelFromXML(parameters.dataSourceName,parameters.entityName)
	parameters<<map
	context.fieldNameList=map.get("field-name")
}

context.jsonData= JSONObject.fromObject(json)
