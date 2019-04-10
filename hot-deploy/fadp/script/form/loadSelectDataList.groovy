import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.common.AjaxCURD
import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.model.ModelEntity
import com.hanlin.fadp.entity.util.EntityQuery

import javax.servlet.http.HttpServletResponse

def delegator = delegator as Delegator
def response = response as HttpServletResponse
def parameters = parameters as Map
def selectFieldList = parameters.selectFieldList as List<Map>
def returnMap = [:]
selectFieldList.each { fieldInfo ->
    def queryContext = [viewSize: 100]
    def dicId = fieldInfo.dicId
    if (dicId) { // 若是字典表，则无需上传如下三个参数
        fieldInfo.entityName = "FadpDic"
        fieldInfo.relFieldName = "dicId"
        fieldInfo.relFieldValue = "dicValue"

        queryContext.entityName = "FadpDic"
        queryContext.condition = [conditionList: [[lhs: 'parentId', operator: "EQUALS", rhs: fieldInfo.dicId]], operator: 'AND']
    } else {
        queryContext.entityName = fieldInfo.relEntityName
        queryContext.condition = fieldInfo.condition
        if (!queryContext.condition) {
            queryContext.condition = [conditionList: [], operator: 'AND']
        }
    }
    if (!queryContext.entityName) {
        return
    }
    def selectList = AjaxCURD.findListByJsonCondition(delegator, queryContext)
    def newList = []
    selectList.list.each { item ->
        newList << [id: item[fieldInfo.relFieldName], value: item[fieldInfo.relFieldValue]]
    }
    returnMap.put(fieldInfo.fieldName, newList)
}
response.getWriter().print(JSON.from(returnMap).toString())




return "success"

//class FieldInfo {
//    def fieldName
//    def PK
//    def fieldLabel
//    def fieldType
//    def showInQuery
//    def showInTable
//    def showInView
//    def showInEdit
//    def entityName
//    def relEntityName
//    def relFieldName
//    def relFieldValue
//    def condition;
//
//}
//
