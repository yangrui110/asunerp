import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.base.util.UtilValidate
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.condition.EntityOperator
import com.hanlin.fadp.entity.util.EntityQuery

import javax.servlet.http.HttpServletResponse

def delegator = delegator as Delegator
def response = response as HttpServletResponse
def deptId = parameters.deptId
def groupId = parameters.groupId

def userSecurityGroupList = EntityQuery.use(delegator).from('UserLoginSecurityGroup').where(EntityCondition.makeCondition('groupId',EntityOperator.EQUALS,groupId)).queryList()
def conditionList=[EntityCondition.makeCondition('deptId',deptId)]
if (UtilValidate.isNotEmpty(userSecurityGroupList)) {
    def userLoginIdList=[]
    userSecurityGroupList.each {usg->
        userLoginIdList<<usg.userLoginId
    }
    conditionList<<EntityCondition.makeCondition('userLoginId',EntityOperator.NOT_IN,userLoginIdList)
}

def userDeptList = EntityQuery.use(delegator).from('DeptUserView').where(conditionList).queryList()

response.getWriter().print(JSON.from(userDeptList).toString())
