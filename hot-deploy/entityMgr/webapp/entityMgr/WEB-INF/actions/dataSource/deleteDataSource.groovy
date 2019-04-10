import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.condition.EntityOperator
import com.hanlin.fadp.entity.util.EntityQuery
import com.hanlin.fadp.petrescence.datasource.DataSourceWorker;
if(parameters["dataSourceName"] instanceof String){
	parameters["dataSourceName"]=[parameters["dataSourceName"]]
}
def con=EntityCondition.makeCondition("dataSourceName", EntityOperator.IN, parameters.dataSourceName)
def list = (from("DatabaseSeq") as EntityQuery).where(con).queryList()
list.each {GenericValue value->
	value.remove()
}

return "success"
