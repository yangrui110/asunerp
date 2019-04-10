import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.common.AjaxCURD
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.util.EntityQuery
import com.hanlin.fadp.security.Security

import javax.servlet.http.HttpServletResponse

def delegator=delegator as Delegator
def response=response as HttpServletResponse
def security=security as Security
def userLogin=userLogin as GenericValue

def queryMap=[entityName: 'SpmProjectView',
              viewSize: 100000,
              viewIndex: 0,
              noConditionFind: 'Y',
              hasTimestamp: 'N',
              condition: [:]]
if(!security.hasPermission("FULL_ADMIN",session)){
    queryMap.condition=[ lhs: 'managerId', operator: 'EQUALS', rhs: userLogin.userLoginId]
}
def data=AjaxCURD.findListByJsonCondition(delegator,queryMap)

response.getWriter().print(JSON.from(data).toString())
