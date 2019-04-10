import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.Delegator;


delegator=(Delegator)delegator
dbs=delegator.findList("DatabaseSeq", null, null, null, null, false)
if(!dbs){
	return
}
dbList=[]
dbs.each{
	myDelegator=DelegatorFactory.getDelegator(it.dataSourceName)
	entityMap=myDelegator.getModelEntityMapByGroup(it.dataSourceName)
	item=[dataSourceName:it.dataSourceName]
	if(parameters.filterByEntityName){
		item["entityNames"]=[]
		entityMap.keySet().each{
			if(it.toUpperCase().contains(parameters.filterByEntityName.toUpperCase())){
				item["entityNames"]<<it
			}
		}
	}else{
		item["entityNames"]=entityMap.keySet()
	}
	dbList << item
}
context.dbList=dbList

