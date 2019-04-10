import com.hanlin.fadp.base.util.UtilMisc
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.DelegatorFactory
import com.hanlin.fadp.entity.jdbc.SQLProcessor
import com.hanlin.fadp.entity.util.EntityQuery

//println "parameters.dataSourceName:"+parameters.dataSourceName
delegator=(Delegator)delegator
parameters=(Map)parameters
if(parameters.extractModelId&&request.getMethod().equalsIgnoreCase("get")){//表示修改界面
	context.model=(from("ExtractModel") as EntityQuery).where([extractModelId:parameters.extractModelId]).queryFirst()
	context.model.each{k,v->
		parameters[k]=v;
	}
	logInfo "context.model:${context.model}"
}
if(parameters.testing=="Y"){//表示测试
	logInfo(parameters.dataSourceName)
	def myDelegator = DelegatorFactory.getDelegator(parameters.dataSourceName)
	println (myDelegator.getModelReader().entityCache)
	def columns = [];
	def records = [];
	context.columns=columns;
	context.records=records;
	def sqlCommand=parameters.modelSql,rowLimit=1000,rs = null,totalRow=0;
	def sqlProcessor = new SQLProcessor(myDelegator,myDelegator.getGroupHelperInfo(myDelegator.getDelegatorName()));
	if(parameters.containsKey("paraValue")){
		def paraValue=UtilMisc.toList(parameters.paraValue);
		parameters.modelParaList=[]
		if(parameters.paraValue instanceof String){
			parameters.modelParaList<<[paraAlias:parameters.paraAlias,paraValue:parameters.paraValue]
		}else{
		for(int i=0;i<parameters.paraValue.size();i++){
			parameters.modelParaList<<[paraAlias:parameters["paraAlias"][i],paraValue:parameters["paraValue"][i]]
			}
		}
		paraValue.each{para->
			sqlCommand=(sqlCommand as String)
			int index=sqlCommand.indexOf("\\%")
			sqlCommand=sqlCommand.substring(0,index)+para+sqlCommand.substring(index+2)
//			sqlCommand=sqlCommand.replaceFirst("\\\\%",para)
		}
	}
	if (sqlCommand.toUpperCase().startsWith("SELECT")) {
		logInfo "数据抽取模型中的sql：$sqlCommand"
		rs = sqlProcessor.executeQuery(sqlCommand);
		if (rs) {
			
			rsmd = rs.getMetaData();
			numberOfColumns = rsmd.getColumnCount();
			for (i = 1; i <= numberOfColumns; i++) {
				columns.add(rsmd.getColumnName(i));
			}
			rowLimitReached = false;
			while (rs.next()) {
				totalRow++;
				if (records.size() >= rowLimit) {
					resultMessage = "Returned top $rowLimit rows.";
					rowLimitReached = true;
					continue;
				}
				record = [];
				for (i = 1; i <= numberOfColumns; i++) {
					record.add(rs.getObject(i));
				}
				records.add(record);
			}
			parameters.totalRow=totalRow
			parameters.currentPageRow=records.size()
			rs.close();
		}
	}
}
