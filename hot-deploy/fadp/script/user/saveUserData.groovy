import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.common.AjaxCURD
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.util.EntityQuery
import com.hanlin.fadp.security.Security

import javax.servlet.http.HttpServletResponse

def delegator = delegator as Delegator
def response = response as HttpServletResponse
def entityName = "UserLogin"
GenericValue user = AjaxCURD.parseValue(delegator, entityName, parameters.fieldMap)
if(!user.workNumber||!user.userId){
    def id = delegator.getNextSeqId(entityName)
    if(!user.workNumber){
        user.setString('workNumber', id)
    }
    if(!user.userId){
        user.setString('userId', id)
    }
}
delegator.createOrStore(user)
response.getWriter().print(JSON.from(user).toString())
