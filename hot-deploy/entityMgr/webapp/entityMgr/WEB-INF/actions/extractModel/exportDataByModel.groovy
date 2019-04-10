import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.common.FindServices;
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.DelegatorFactory
import com.hanlin.fadp.entity.jdbc.SQLProcessor;
import com.hanlin.fadp.service.GenericDispatcherFactory;;;

parameters=(Map)parameters
delegator=(Delegator)delegator
def dataModel=delegator.findOne("DatabaseModel",false,"dataModelId",parameters.dataModelId)
def response=(HttpServletResponse)response
response.setContentType("text/comma-separated-values; charset=GBK");// 设置response内容的类型
response.setHeader(
		"Content-disposition",
		"attachment;filename=${dataModel.dataModelId}.csv");// 设置头部信息
def writer=response.getWriter()


def myDelegator=DelegatorFactory.getDelegator(dataModel.dataSourceName)
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
def columns = [];
def records = [];
def resultMessage
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
				rsmd = rs.getMetaData();
				numberOfColumns = rsmd.getColumnCount();
				for (i = 1; i <= numberOfColumns; i++) {
					if(i!=1){
						writer.print ","
					}
					writer.print fieldMapping[rsmd.getColumnName(i)]
					columns.add(rsmd.getColumnName(i));
				}
				writer.println()
				rowLimitReached = false;
				while (rs.next()) {
					if (records.size() >= rowLimit) {
						resultMessage = "Returned top $rowLimit rows.";
						rowLimitReached = true;
						break;
					}
					record = [];
					for (i = 1; i <= numberOfColumns; i++) {
						if(i!=1){
							writer.print ","
						}
						writer.print rs.getObject(i)
						record.add(rs.getObject(i));
					}
					writer.println()
					records.add(record);
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
	isHead=true
	modelEntity.getFieldsUnmodifiable().each {
		if(!timesStampSet.contains(it.getName())){
			if(!isHead){
				writer.print ","
			}else{
				isHead=false;
			}
			writer.print fieldMapping[it.getName()]
			columns<<it.getName()
		}
	}
	writer.println()

	def dispatcher = new GenericDispatcherFactory().createLocalDispatcher(myDelegator.getDelegatorName(), myDelegator)
	def myContext=[:]
	myContext.inputFields=dataModel.modelCondition
	myContext.entityName=dataModel.get("myEntityName")
	myContext.noConditionFind="Y"
	myContext.viewSize=1000000
	myContext.fieldList=columns
	println myContext
	findResultMap =FindServices.performFindList(dispatcher.getDispatchContext(), myContext)
	println findResultMap
	if(findResultMap.list){
		findResultMap.list.each{entity->
			record=[]
			isHead=true
			columns.each{field->
				if(!isHead){
					writer.print ","
				}else{
					isHead=false;
				}
				writer.print entity[field]
				record<<entity[field]
			}
			writer.println()
			records<<record
		}
	}
}
println records
response.flushBuffer()