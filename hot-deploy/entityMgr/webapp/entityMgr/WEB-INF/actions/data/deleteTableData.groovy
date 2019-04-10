import javax.servlet.http.HttpServletResponse;
import com.hanlin.fadp.entity.DelegatorFactory
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.condition.EntityOperator;
import com.hanlin.fadp.entity.model.ModelField;


def parameters=parameters
def myDelegator=DelegatorFactory.getDelegator(parameters.dataSourceName)
if(parameters.pk instanceof String){
	parameters.pk=[parameters.pk]
}
def pkList=[]
parameters.pk.each{pkString->
	def arr=pkString.split("&");
	def pk=[:]
	arr.each{kvPair->
		if(kvPair){
			def kv=kvPair.split("=")
			pk[kv[0].substring(3)]=kv[1]
		}
	}
	pkList<<EntityCondition.makeCondition(pk)
}
def condition=EntityCondition.makeCondition(pkList,EntityOperator.OR)
if(pkList.size()==0){
	return "error"
}else{
	myDelegator.removeByCondition(parameters.entityName,condition)
}
return "success"
