import com.hanlin.fadp.base.util.StringUtil
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.util.EntityQuery;
import com.hanlin.fadp.petrescence.datasource.EntityModelWorker

import groovy.json.JsonOutput;;
def dataSourceNameList= (from("DatabaseSeq") as EntityQuery).queryList()
def jsonData=[]
dataSourceNameList.each{item->
	def entityList=[]
	if(item.dataSourceName=="default"){
		(delegator as Delegator).getModelReader().getEntityNames().each{entityName->
			entityList<<[type:"file","text":entityName,children:false,"a_attr":[href:"""entityModel?dataSourceName=${item["dataSourceName"]}&entityName=${entityName}"""]]
		}
	}else{
		try{

			def list=EntityModelWorker.readEntityModelListFromXML(dataSourceName:item["dataSourceName"])
			list.each{entity->
				entityList<<[type:"file","text":entity["entityName"],children:false,"a_attr":[href:"""entityModel?dataSourceName=${item["dataSourceName"]}&entityName=${entity["entityName"]}"""]]
			}
		}catch(e){
			logError(e.toString())
		}
	}


	def dsText=item["dataSourceName"]+" "+item["description"]+" "+item["fieldTypeName"]+":"+item["ip"]+":"+item["port"]+"/"+item["databaseName"]
	jsonData<<[id:item["dataSourceName"],type:"root","text":dsText,children:entityList]
}
context.json=StringUtil.wrapString(JsonOutput.toJson(jsonData))
