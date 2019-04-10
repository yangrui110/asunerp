import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.common.AjaxCURD
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.util.EntityQuery
import com.hanlin.fadp.security.Security

import javax.servlet.http.HttpServletResponse

def delegator=delegator as Delegator
def response=response as HttpServletResponse
def userLoginId=parameters.condition.userLoginId

def user = EntityQuery.use(delegator).from('UserView').where("userLoginId", userLoginId).queryOne()

response.getWriter().print(JSON.from(user).toString())
