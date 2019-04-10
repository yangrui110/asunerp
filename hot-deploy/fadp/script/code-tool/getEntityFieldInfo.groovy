import com.hanlin.fadp.base.util.UtilMisc
import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.model.ModelEntity
import com.hanlin.fadp.entity.util.EntityQuery

import javax.servlet.http.HttpServletResponse

def delegator = delegator as Delegator
def response = response as HttpServletResponse
def parameters = parameters as Map
entityName = parameters.entityName
def returnObj

fieldModelMap = [:]
ModelEntity modelEntity = delegator.getModelReader().entityCache.get(entityName)
returnObj = makeEntityFieldInfoFromXML(modelEntity)


def fieldInfoListAll = EntityQuery.use(delegator).orderBy('entityName', 'orderNum').where(entityName: entityName).from("EntityFieldInfo").queryList()
fieldInfoListAll.each { obj ->
    def field = fieldModelMap.get("${obj.entityName}/${obj.fieldName}")
    if (field == null) return
    def notNull = field.required
    field.putAll(obj)
    if (notNull == 'Y') {
        field.required = 'Y'
    }
}
returnObj.fieldList = UtilMisc.sortMaps(returnObj.fieldList, ['orderNum'])

AjaxUtil.writeJsonToResponse(returnObj, response)


def makeEntityFieldInfoFromXML(ModelEntity modelEntity) {
    def obj = [entityName: modelEntity.entityName, description: modelEntity.description, fieldList: []]
    def allFields = modelEntity.getFieldsUnmodifiable()
    def allFieldNames = []
    allFields.each { field ->
        allFieldNames << field.getName()
    }
    allFieldNames.removeAll(modelEntity.automaticFieldNames)
    allFieldNames.eachWithIndex { fieldName, index ->
        def modelField = modelEntity.getField(fieldName)
        def fieldObj = [
                entityName: modelEntity.entityName, fieldName: fieldName,
                fieldLabel: modelField.description, description: modelField.description,
                PK        : modelField.isPk, required: modelField.isNotNull ? 'Y' : 'N',
                orderNum  : (index + 1)+""
        ]
        obj.fieldList << fieldObj
        fieldModelMap.put("${modelEntity.entityName}/$fieldName", fieldObj)
    }
    return obj
}