import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.condition.EntityCondition

import javax.servlet.http.HttpServletResponse

def delegator =delegator as Delegator
pkList= parameters.pkList as List
pass= parameters.password
if(pkList!=null&&pkList.size()!=0){
    def entityList=[]
    pkList.each {item->
        item.currentPassword=pass
        entityList<<delegator.makeValue("UserLogin",item)
    }
    delegator.storeAll(entityList)
}
def writer = (response as HttpServletResponse).writer

writer.print('{}')
writer.close();
return "success"
