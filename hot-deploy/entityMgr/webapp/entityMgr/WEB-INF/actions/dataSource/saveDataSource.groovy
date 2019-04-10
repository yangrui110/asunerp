import com.hanlin.fadp.base.util.UtilXml
import com.hanlin.fadp.entity.config.model.EntityConfig
import com.hanlin.fadp.petrescence.datasource.DataSourceWorker
import com.hanlin.fadp.petrescence.datasource.EntityEngineUtil
import org.w3c.dom.Element

import java.sql.Driver
import java.sql.DriverManager
import java.sql.Statement

def parameters=(Map)parameters

if(request.getMethod().equalsIgnoreCase("post")){
	if(parameters.testing=="Y"){
		return testing()
	}else{
		def msg=testing()
		if(msg._response_code_=="error"){
			return msg
		}else{
			if(!parameters.fieldTypeName||!parameters.schemaName||!parameters.databaseName||!parameters.ip){
				return [_error_message_:"请填写完整必填字段",_response_code_:"error"]
			}
			parameters["schema-name"]=parameters.schemaName
			parameters["field-type-name"]=parameters.fieldTypeName
			parameters["jdbc-username"]=parameters.jdbcUsername
			parameters["jdbc-password"]=parameters.jdbcPassword
			parameters["jdbc-uri"]=msg.jdbcInfo.jdbcUri
			if(parameters.dataSourceName==null){
				map=DataSourceWorker.addDataSource(dispatcher.getDispatchContext(), parameters);
			}else{
				map=DataSourceWorker.updateDataSource(dispatcher.getDispatchContext(), parameters);
			}
			println "执行添加或修改数据源后的返回值${map}"
			if(map.errorMessage){
				return [_error_message_:map.errorMessage,_response_code_:"error"]
			}else{
				//				Start.restart();
				return "success"
			}
		}
	}
}else{
	return "get"
}

def testing(){
	try{
		def map=getJdbcInfo()
		println map

		def conn=DriverManager.getConnection(map.jdbcUri, map.jdbcUsername, map.jdbcPassword);
		if(parameters.fieldTypeName=="derby"){
			Statement st = conn.createStatement();
			def md=conn.getMetaData();
			def rs=md.getTables(null, null, null, ["TABLE"] as String[]); 
		}
		return [_event_message_:"测试成功",_response_code_:"testing",jdbcInfo:map]
	}catch(e){
		e.printStackTrace()
		return [_error_message_:"测试失败"+e.getMessage(),_response_code_:"error"]
	}
}
def getJdbcInfo(){

	def  jdbcDriver, jdbcUri

	Element templateDatasource = DataSourceWorker.getTemplateDatasource(parameters.fieldTypeName)
	Element jdbcEle = UtilXml.firstChildElement(templateDatasource, "inline-jdbc")
	jdbcDriver=jdbcEle.getAttribute("jdbc-driver")
	jdbcUri=jdbcEle.getAttribute("jdbc-uri")


	jdbcUri=jdbcUri.replace("localhost","127.0.0.1").replace("test",parameters.databaseName).replace("ofbiz",parameters.databaseName)
	if(jdbcUri.indexOf("127.0.0.1")>0){
		def uriHF=jdbcUri.split("127.0.0.1:\\d*")
		jdbcUri=(uriHF[0]+parameters.ip+":"+parameters.port+uriHF[1])
	}
	return [   "jdbcUri":jdbcUri,
		"jdbcDriver":jdbcDriver,
		"jdbcUsername":parameters.jdbcUsername,
		"jdbcPassword":parameters.jdbcPassword ]
}



