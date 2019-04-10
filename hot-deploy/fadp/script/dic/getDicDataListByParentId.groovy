import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.model.ModelEntity
import com.hanlin.fadp.entity.util.EntityQuery

import javax.servlet.http.HttpServletResponse

def delegator = delegator as Delegator
def response = response as HttpServletResponse
def entity = delegator.getModelEntity("FadpDic")
def selectFields = (entity.getAllFieldNames() - entity.getAutomaticFieldNames()) as HashSet
def conditionMap=["parentId": parameters.parentId]
if(parameters.enabled){
    conditionMap.put('enabled', parameters.enabled)
}
def entityList = EntityQuery.use(delegator).from("FadpDic").select(selectFields).where(conditionMap).orderBy('orderNum').queryList()
if (entityList == null) {
    entityList = []
}
response.getWriter().print(JSON.from(entityList).toString())
