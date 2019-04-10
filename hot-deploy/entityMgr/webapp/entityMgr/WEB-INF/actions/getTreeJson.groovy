import javax.servlet.http.HttpServletResponse

import com.hanlin.fadp.base.util.Debug
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.condition.EntityOperator
import com.hanlin.fadp.entity.datasource.GenericHelper
import com.hanlin.fadp.entity.model.DynamicViewEntity
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.entity.util.EntityQuery
import com.hanlin.fadp.petrescence.datasource.EntityModelWorker;

import groovy.json.JsonOutput;

Delegator delegator=delegator

TransactionUtil.commit();
def currentTime=System.currentTimeMillis()
Map paras=parameters
String key=paras["id"]
def jsonData=[]
if(key=="#"){
//	def dataSourceNameList= (from("DatabaseSeq") as EntityQuery).queryList()
			def dataSourceNameList= (from("DatabaseSeq") as EntityQuery).where('isShow','Y').queryList()
	dataSourceNameList.each{item->

		//		def dsText=item["dataSourceName"]+" "+item["description"]+" "+item["fieldTypeName"]+":"+item["ip"]+":"+item["port"]+"/"+item["databaseName"]
		def dsText=item["description"]+"("+item["dataSourceName"]+")"
		if(dsText=="default"){
			dsText=item["description"];
		}
		jsonData<<[id:item["dataSourceName"],type:"root","text":dsText,children:true]
	}
	jsonData<<[id:delegator.getDelegatorName(),type:"root","text":"系统数据库",children:true]
}else{
//key="default"
	Delegator myDelegator=DelegatorFactory.getDelegator(key);
	def list;
	if(key.startsWith("default")){
		list=myDelegator.getModelReader().entityCache.values();	
	}else{
		list=EntityModelWorker.readEntityModelListFromXML(dataSourceName:key)
	}
	list.each{entity->
		def entityName=entity["entityName"]
		jsonData<<[id:key+"::"+	entityName,type:"file","text":entityName,children:false,
			"a_attr":[
				href:"##",dataSourceName:key,
				entityName:entityName,
				description:entity.description
				, totalRow:0
//				, totalRow:EntityQuery.use(myDelegator).from(entityName).queryCount()
				]]
	}
}


def res=response as HttpServletResponse
res.setContentType("application/json");
res.writer.print(JsonOutput.toJson(jsonData))
Debug.log("构造树用时::$key  ${System.currentTimeMillis()-currentTime}毫秒")