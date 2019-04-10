import java.io.File;
import java.nio.file.Files;

import org.w3c.dom.Document;

import com.hanlin.fadp.base.location.ComponentLocationResolver;
import com.hanlin.fadp.base.util.FileUtil;
import com.hanlin.fadp.base.util.UtilMisc
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.common.FindServices;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.transaction.TransactionUtil
import com.hanlin.fadp.service.GenericDispatcherFactory
import com.hanlin.fadp.service.ServiceContainer;
import com.hanlin.fadp.petrescence.datasource.BaseInfo
import com.hanlin.fadp.petrescence.datasource.DataSourceWorker;
import com.hanlin.fadp.entity.model.*
def getMiss(){
	
}
def set=new HashSet()
def findEntity(set,entityName){
	def delegator=(Delegator)delegator
	def entity=delegator.getModelEntity(entityName)
	if(!set.contains(entityName)){
		set<<entityName
		def list=entity.getRelationsOneList()
		if(list!=null){
			for(relation in list){
				def en=relation.getModelEntity().getEntityName()
				def ren= relation.getRelEntityName()
				if(relation.getModelEntity().getEntityName()!=relation.getRelEntityName()){
					findEntity(set,relation.getRelEntityName())
				}else{
					break
				}
			}
		}
	}
}
def findMiss(set){
	findEntity(set,"UserLogin")
	findEntity(set,"JobSandbox")
	findEntity(set,"JobInterview")
	set<<"SequenceValueItem"
	set<<"DatabaseSeq"
	set<<"CommunicationEvent"
	com.hanlin.fadp.petrescence.datasource.FindMissedEntity.writeXML(set)
}
//findMiss(set);
def parameters =(Map)parameters
def curDelegator,curDispatcher
if(context.myDelegator){
	println context.myDelegator
	def time=0;
	while(curDelegator==null){
		curDelegator =DelegatorFactory.getDelegator(context.myDelegator)
		sleep(100)
		time+=1;
		if(time>50){
			return "error"
		}
	}
	curDispatcher=ServiceContainer.getLocalDispatcher(curDelegator.getDelegatorName(), curDelegator)
}else{
	curDelegator =(Delegator)delegator
	curDispatcher=dispatcher
}
//context.entityName="DatabaseModel"
//context.fieldList=BaseInfo.getFieldSetWidthOutTimeStamp(curDelegator.getModelEntity(entityName))

if(parameters.pageSize==null){
	parameters.pageSize=10
}else if(parameters.pageSize instanceof String ){
	parameters.pageSize=Integer.parseInt(parameters.pageSize)
}
if(parameters.currPage==null){
	parameters.currPage=1;
}else if(parameters.currPage instanceof String ){
	parameters.currPage=Integer.parseInt(parameters.currPage)
}
def entityModel=curDelegator.getModelEntity(context.entityName);
if(!entityModel.pkFieldNames){
	context.fieldList=entityModel.allFieldNames;
}else{
	context.fieldList=BaseInfo.getFieldSetWidthOutTimeStamp(entityModel).toList()
}
context.inputFields=parameters
context.viewSize=parameters.pageSize
context.viewIndex=parameters.currPage-1
context.noConditionFind="Y"

map=FindServices.performFindList(curDispatcher.getDispatchContext(), context);

context.list=map.list
context.pageSize=parameters.pageSize!=null?parameters.pageSize:10
context.currPage=parameters.currPage!=null?parameters.currPage:10
context.totalRow=map.listSize
context.totalPages=Math.ceil(map.listSize/(parameters.pageSize))
delegator=(Delegator)delegator
def pageSizeEntities=delegator.findList("Dictionary",EntityCondition.makeCondition([EntityCondition.makeCondition(["module":"pageSize"])]), null, null, null, false)
def pageSizeList=[]
if(pageSizeEntities){
	pageSizeEntities.each{
		pageSizeList << Integer.parseInt(it.value)
	}
	pageSizeList.sort()
}else{
	pageSizeList=[10,20,50]
}
context.pageSizeList=pageSizeList