import com.hanlin.fadp.base.VisitHelper
import com.hanlin.fadp.webapp.control.ConfigXMLReader
import com.hanlin.fadp.webapp.control.RequestHandler

import javax.servlet.http.HttpServletRequest

def request=request as HttpServletRequest
def parameters=parameters as Map
def requestHandler=parameters._REQUEST_HANDLER_ as RequestHandler
String entityName = parameters.entityName
String targetRequestUri = parameters.targetRequestUri
def requestMap = requestHandler.getControllerConfig().getRequestMapMap().get(RequestHandler.getRequestUri(targetRequestUri))
switch (targetRequestUri){
    case '/getDicTreeData':
        VisitHelper.writeVisit(request,'字典管理','查看','获取字典树数据')
        break
    case '/getDicDataListByParentId':
        break
    case '/getPageData':
        break
    case '/genericSave':
        break
    case '/genericSaveAll':
        break
    case '/genericDelete':
        break
    default:
        VisitHelper.writeVisit(request,requestMap.uri,'操作',requestMap.description)


        break

}
if(entityName=="FadpDic"){

}
return 'success'