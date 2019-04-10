import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.common.AjaxCURD
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.util.EntityQuery

import javax.servlet.http.HttpServletResponse

def delegator=delegator as Delegator
def response=response as HttpServletResponse
delegator.removeByAnd("SecurityGroupPermission",["groupId":parameters.groupId])
if(parameters.permissionList){
    def entityList=[]
    parameters.permissionList.each{item->
        item.groupId=parameters.groupId
        entityList<<delegator.makeValidValue("SecurityGroupPermission",item)
    }
    delegator.storeAll(entityList)
}
response.getWriter().print("{}")
