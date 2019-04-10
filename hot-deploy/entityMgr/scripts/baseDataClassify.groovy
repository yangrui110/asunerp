import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.base.util.StringUtil
import com.hanlin.fadp.entity.util.EntityQuery

import javax.servlet.http.HttpServletRequest

def dataSourceNameList= (from("DatabaseSeq") as EntityQuery).where('isShow','Y').queryList()
def jsonData=[]
dataSourceNameList.each{item->
    def dsText=item["description"]+"("+item["dataSourceName"]+")"
    if(dsText=="default"){
        dsText=item["description"];
    }
    jsonData<<[id:item["dataSourceName"],type:"root","text":dsText,children:true]
}
context.json=StringUtil.wrapString(JSON.from(jsonData).toString())
