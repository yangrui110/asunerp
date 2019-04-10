import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.model.ModelEntity

import javax.servlet.http.HttpServletResponse

def delegator = delegator as Delegator
def response = response as HttpServletResponse

def entityModelListAll = delegator.getModelReader().entityCache.values()
def entityModelList = []
entityModelListAll.each { ModelEntity modelEntity ->
    if (modelEntity.location.indexOf("framework") < 0) {
        entityModelList << [entityName: modelEntity.entityName, description: modelEntity.description]
    }
}
AjaxUtil.writeJsonToResponse(entityModelList, response)

