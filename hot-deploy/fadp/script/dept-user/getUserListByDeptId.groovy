import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.model.ModelEntity
import com.hanlin.fadp.entity.util.EntityQuery

import javax.servlet.http.HttpServletResponse
viewEntityName = 'DeptUserView';

def delegator=delegator as Delegator
def response=response as HttpServletResponse
def deptId = parameters.deptId
def conMap=[:]
if (deptId){
    conMap.deptId=deptId
}
def entityList = EntityQuery.use(delegator).from(viewEntityName).where(conMap).queryList()
if(entityList==null){
    entityList=[]
}
response.getWriter().print(JSON.from(entityList).toString())
