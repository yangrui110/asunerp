import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.condition.EntityOperator;;;

def delegator=delegator as Delegator
def pks=parameters["extractModelId"]
if(!pks){
	error("请选择")
}
if(pks instanceof String){
	pks=[pks]
}
delegator.removeByCondition("ExtractModel",EntityCondition.makeCondition("extractModelId",EntityOperator.IN,pks))
return "success"