import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.entity.Delegator

import javax.servlet.http.HttpServletResponse

def delegator = delegator as Delegator
def response = response as HttpServletResponse
def parameters = parameters as Map
def entityList=[];
parameters.deptList.each{dept->
    entityList<<delegator.makeValidValue("Department",dept)
}
def returnMap=[:]
try{
    delegator.storeAll(entityList)
}catch (e){
    returnMap.error=e.getMessage()
}
response.getWriter().print( JSON.from(returnMap).toString())

