import com.hanlin.fadp.base.util.StringUtil;
import com.hanlin.fadp.entity.DelegatorFactory;
def pkList

if(parameters.dataSourceName){
	myDelegator=DelegatorFactory.getDelegator(parameters.dataSourceName)
	entityMap=myDelegator.getModelEntityMapByGroup(parameters.dataSourceName)
	context.entityList=entityMap.keySet().toList()
	//	println "entityList"+context.entityList

	if(parameters.entityName){
		def list=[];
		def textTypes=[];
		def modelEntity = myDelegator.getModelEntity(parameters.entityName)
		def fieldTypeReader = myDelegator.getModelFieldTypeReader(modelEntity)
		fieldTypeReader.getFieldTypes().each {
			if (it.getJavaType()=="String"){
				textTypes<<it.getType()
			}
		}
		context.textTypes=textTypes
		context.dateTypes=['date', 'date-time', 'time']
		timesStampSet=['lastUpdatedStamp', 'lastUpdatedTxStamp', 'createdStamp', 'createdTxStamp']
		modelEntity.getFieldsUnmodifiable().each {
			if(!timesStampSet.contains(it.getName())){
				list<<[label:it.getName(),fieldType:it.getType(),fieldValue:'',fieldName:it.getName()]
			}
		}
		b=false
		pkList=new HashSet(modelEntity.getPkFieldNames()) 
		context.conditions=list
	}
}
if(!pkList){
	pkList=context.fieldList
}
if(context.list){

	columns=context.fieldList
	records=[]
	context.list.each{entity->
		record=[]
		def pkString=""
		columns.each{field->
			if(pkList.contains(field)){
				if(""==pkString){
					pkString+="pk_${field}=${entity[field]}"
				}else{
					pkString+="&pk_${field}=${entity[field]}"
				}
			}
			record<<entity[field]
		}
		record.add(0, StringUtil.wrapString(pkString))
		records<<record
	}
	context.records = records;
}
context.columns = context.fieldList
