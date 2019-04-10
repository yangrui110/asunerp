import javax.servlet.http.HttpServletResponse

import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.DelegatorFactory
import com.hanlin.fadp.entity.jdbc.SQLProcessor;
import com.hanlin.fadp.entity.util.EntityQuery;

import groovy.json.JsonOutput;;
def response = response as HttpServletResponse
response.setContentType("application/json");
def writer=response.writer
def saveDatasourceId=parameters.saveDatasource
def pk=[extractModelId:parameters.extractModelId,saveModelId:parameters.saveModelId];
def extractData=EntityQuery.use(delegator).from("ExtractData").where(pk).queryOne();


def saveDelegator=DelegatorFactory.getDelegator(saveDatasourceId);
def saveModel=saveDelegator.getModelEntity(pk.saveModelId);

def extractModel=(from("ExtractModel") as EntityQuery).where([extractModelId:pk.extractModelId]).queryFirst();
def extractDelegator = DelegatorFactory.getDelegator(extractModel.dataSourceName)
def extractField=[];
def sqlProcessor = new SQLProcessor(extractDelegator,extractDelegator.getGroupHelperInfo(extractDelegator.getDelegatorName()));
def sqlCommand=extractModel.modelSql;
if(extractModel.modelParaList){
	extractModel.modelParaList.each{para->
		int index=sqlCommand.indexOf("\\%")
		sqlCommand=sqlCommand.substring(0,index)+para.paraValue+sqlCommand.substring(index+2)
	}
}
def rs = sqlProcessor.executeQuery(sqlCommand);
if (rs) {
	def rsmd = rs.getMetaData();
	numberOfColumns = rsmd.getColumnCount();
	for (i = 1; i <= numberOfColumns; i++) {
		extractField.add(rsmd.getColumnLabel(i));
	}
	rs.close();
}

def mapping=[];
println extractData;
if(extractData){
	mapping=extractData.fieldMapping;
	mapping.each{map->
		def saveField=map["saveField"];
		def text=saveModel.getField(saveField).getDescription();
		if(!text){
			text=saveField;
		}
		map["saveField"]=[text:text,name:saveField]
	}
}else{

	saveModel.getFieldsUnmodifiable().each{field->
		def name=field.getName()
		def text=field.getDescription()
		if(UtilValidate.isEmpty(text)){
			text=name;
		}
		
		mapping<<[saveField:[name:name,text:text]];
	}
}
println mapping;
writer.print(JsonOutput.toJson([extractField:extractField,fieldMapping:mapping]));


//writer.print(JsonOutput.toJson(["hello":"hello"]))