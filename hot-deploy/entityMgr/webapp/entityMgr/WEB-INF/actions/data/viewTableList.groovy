import com.hanlin.fadp.entity.DelegatorFactory;

myDelegator=DelegatorFactory.getDelegator(parameters.dataSourceName)
entityMap=myDelegator.getModelEntityMapByGroup(parameters.dataSourceName)
set=entityMap.keySet().toList()
list=[]
temp=[]
set.each {
	if(temp.size()<3){
		temp<<it
	}else{
		list<<temp
		temp=[it]
	}
}
if(temp.size()>0){
	list<<temp
}
context.list=list
