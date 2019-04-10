import org.bson.Document

import com.hanlin.fadp.base.util.UtilDateTime;
import com.hanlin.fadp.common.FindServices;
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.jdbc.SQLProcessor
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.petrescence.datasource.MongoService
import com.hanlin.fadp.service.GenericDispatcherFactory
TransactionUtil.commit()
parameters=(Map)parameters
delegator=(Delegator)delegator
if(parameters.syncDataModelId){
	def model=delegator.findOne("DatabaseModel",["dataModelId":parameters.syncDataModelId],false)
	//单个更新 ,此时是当event执行的,需要返回值
	saveSaveModel(model)
	println parameters.syncDataModelId
	return "signle"
}else if(parameters.sync=="Y"){
	//批量更新 , 所以不需要返回值
	def myContext=[:]
	def inputFields=[:]
	def modelEntity = delegator.getModelEntity("DatabaseModel")
	timesStampSet=['lastUpdatedStamp', 'lastUpdatedTxStamp', 'createdStamp', 'createdTxStamp']

	putValid={f->
		if(parameters.containsKey("_fadp_"+f)){
			inputFields[f]=parameters["_fadp_"+f]
		}
	}
	modelEntity.getFieldsUnmodifiable().each {
		if(parameters["asCon_fadp_"+it.getName()]=="Y"){
			putValid(it.getName())
			putValid(it.getName()+"_op")
			putValid(it.getName()+"_fld0_value")
			putValid(it.getName()+"_fld0_op")
			putValid(it.getName()+"_fld1_value")
			putValid(it.getName()+"_fld1_op")
		}
	}
	myContext.inputFields=inputFields
	myContext.entityName="DatabaseModel"
	myContext.noConditionFind="Y"
	rowLimit=parameters.rowLimit
	if(rowLimit==null){
		rowLimit=1000000
	}
	myContext.viewSize=rowLimit
	findResultMap =FindServices.performFindList(dispatcher.getDispatchContext(), myContext)
	if(findResultMap.list){
		findResultMap.list.each{model-> saveSaveModel(model) }
	}
	return "syncByCondition"
}
return "success"
def saveSaveModel(GenericValue dataModel){

	def myDelegator=DelegatorFactory.getDelegator(dataModel.dataSourceName)
	def records = [];
	def resultMessage;
	def fieldMapping=[:]
	if(dataModel.myEntityName){
		dataModel.fieldMapping.each{k,v->
			if(v){
				fieldMapping[k.replace("_fadp_","").replace("_saveField","")]=v
			}
		}
	}else{
		dataModel.fieldMapping.each{k,v->
			fieldMapping[v.replace("_saveField","")]=k
		}
	}
	println "sync fieldMapping"+fieldMapping
	if(dataModel.modelSql){
		//sql优先
		def sqlCommand=dataModel.modelSql
		def rowLimit=parameters.rowLimit
		if(rowLimit==null){
			rowLimit=1000000
		}
		def rs = null;
		def du = new SQLProcessor(myDelegator.getGroupHelperInfo(myDelegator.getDelegatorName()));
		try {
			if (sqlCommand.toUpperCase().startsWith("SELECT")) {
				rs = du.executeQuery(sqlCommand);
				if (rs) {
					def rsmd = rs.getMetaData();
					def numberOfColumns = rsmd.getColumnCount();
					def columns = [];
					for (i = 1; i <= numberOfColumns; i++) {
						columns<<fieldMapping[rsmd.getColumnName(i)]
					}
					def rowLimitReached = false;
					while (rs.next()) {
						if (records.size() >= rowLimit) {
							resultMessage = "Returned top $rowLimit rows.";
							rowLimitReached = true;
							break;
						}
						def record =new Document();
						for(int i;i<columns.size();i++){
							def fieldName=columns[i]
							record.append(fieldName,rs.getObject(i))
						}
						records<<record
					}
					resultMessage = "Returned " + (rowLimitReached? "top " + rowLimit : "" + records.size()) + " rows.";
					rs.close();
				}
			}
		} catch (Exception exc) {
			resultMessage = exc.getMessage();
		}
		//	return [_event_message_:resultMessage,_response_code_:"testing"]
	}else{
		def modelEntity = myDelegator.getModelEntity(dataModel.myEntityName)
		timesStampSet=['lastUpdatedStamp', 'lastUpdatedTxStamp', 'createdStamp', 'createdTxStamp']
		def dispatcher = new GenericDispatcherFactory().createLocalDispatcher(myDelegator.getDelegatorName(), myDelegator)
		def myContext=[:]
		myContext.inputFields=dataModel.modelCondition
		myContext.entityName=dataModel.get("myEntityName")
		myContext.noConditionFind="Y"
		myContext.viewSize=1000000
		println myContext
		def findResultMap =FindServices.performFindList(dispatcher.getDispatchContext(), myContext)
		println findResultMap
		if(findResultMap.list){
			findResultMap.list.each{entity->
				def record =new Document();
				fieldMapping.each{k,v->
					record.append(v,entity[k])
				}
				records<<record
			}
		}
	}
//	println "records::"+records
	MongoService.insertMany(dataModel.saveModelId, records)
	dataModel.syncTime=UtilDateTime.nowTimestamp()
	delegator.store(dataModel)
}