import com.hanlin.fadp.base.util.UtilDateTime
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.DelegatorFactory
import com.hanlin.fadp.entity.jdbc.SQLProcessor
import com.hanlin.fadp.entity.util.EntityQuery
import groovy.json.JsonOutput

import javax.servlet.http.HttpServletResponse

def response = response as HttpServletResponse
def parameters=parameters as Map
response.setContentType("application/json");
def writer=response.writer
def extractModel=(from("ExtractModel") as EntityQuery).where('extractModelId',parameters.extractModelId).queryOne();
Delegator delegator= delegator as Delegator
def extractDataPk=delegator.makeValidValue("ExtractData",parameters).getPrimaryKey()
def extractData=(from("ExtractData") as EntityQuery).where(extractDataPk).queryOne();
def syncType=extractData.syncType;

def hasPara=syncType.contains('参数');
def justAdd=!syncType.contains('更新');
def dependOnTime=syncType.contains('时间');//sql中包含$lastSyncTime

def extractDelegator = DelegatorFactory.getDelegator(extractModel.dataSourceName);
def saveDelegator = DelegatorFactory.getDelegator(extractDataPk.saveDatasource);

def sqlProcessor = new SQLProcessor(extractDelegator,extractDelegator.getGroupHelperInfo(extractDelegator.getDelegatorName()));
def sqlCommand=extractModel.modelSql;
if(parameters.paraList){
	parameters.paraList.each{para->
		int index=sqlCommand.indexOf("\\%")
		sqlCommand=sqlCommand.substring(0,index)+para['paraValue']+sqlCommand.substring(index+2)
	}
}
if(dependOnTime){
	sqlCommand=sqlCommand.replace("\$lastSyncTime",extractData.lastSyncTime);
}
def rs = sqlProcessor.executeQuery(sqlCommand);
def error
if (rs) {
	def rsmd = rs.getMetaData();
	def temp2=[:];//列的索引
	def numberOfColumns = rsmd.getColumnCount();
	for (i = 1; i <= numberOfColumns; i++) {
		temp2[rsmd.getColumnLabel(i)]=i;
	}
	def fieldMapping=extractData['fieldMapping'];
	def temp3=[:]//以抽取模型列为key
	def defaultFieldList=[];//在抽取模型中不存在的字段
	fieldMapping.each{item->
		if(item['extractField']){
			temp3[temp2[item['extractField']]]=item
		}else{
		defaultFieldList<<item;
		}
		
	}
	def toSaveList=[]
	while (rs.next()) {
		def record = saveDelegator.makeValue(extractData.saveModelId)
		for (i = 1; i <= numberOfColumns; i++) {
			def fieldMappingItem=temp3[i];
			if(!fieldMappingItem){
				break;
			}
			def value=rs.getObject(i);
			if(value==null){
				value=fieldMappingItem['defaultValue']
			}
			record.setString(fieldMappingItem['saveField'], value+"")
		}
		defaultFieldList.each{field->
			record.setString(field['saveField'],field["defaultValue"]+"");
		}
		try{
			if(justAdd){
				record.create();
			}else{
				saveDelegator.createOrStore(record);
			}
			toSaveList<<record;
		}catch(e){
			error="部分数据同步失败,发生如下错误：<br/>"+e.getMessage();
		}

	}
//	println toSaveList;
	saveDelegator.storeAll(toSaveList);
	extractData['lastSyncTime']=UtilDateTime.nowTimestamp();
	extractData.store();
	rs.close();
}
if(error){
	writer.print(JsonOutput.toJson([error:error]));
}else{
	writer.print(JsonOutput.toJson([:]));
}


