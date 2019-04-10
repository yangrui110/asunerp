import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.common.AjaxCURD
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.util.EntityQuery

import javax.servlet.http.HttpServletResponse

def delegator = delegator as Delegator
def response = response as HttpServletResponse
def parameters = parameters as Map
String entityName = parameters.entityName
String idField = parameters.idField
String parentField = parameters.parentField
String valueField = parameters.valueField
Map jsonCondition = parameters.condition
def condition
if (jsonCondition) {
    condition = new AjaxCURD.ParseMapCondition(delegator, entityName).parseToCondition(jsonCondition)
} else {
    condition = [:]
}
def entityList = EntityQuery.use(delegator).from(entityName).where(condition).orderBy(parentField).queryList()
if (entityList) {
    def worker = new DicWorker(entityList, idField, parentField, valueField)
    def jsonStr = JSON.from(worker.treeData).toString()
    println(jsonStr)
    response.getWriter().print(jsonStr)
}


class DicWorker {
    String idField
    String parentField
    String valueField

    def dicList = null
    def dicMap = [:]
    def dicTreeMap = [root: [id: 'root', value: "树", children: []]]

    DicWorker(dicList, idField, parentField, valueField) {
        this.idField = idField
        this.parentField = parentField
        this.valueField = valueField
        this.dicList = dicList
        dicList.each { dicEntity ->
            def id = dicEntity.getString(this.idField)
            dicMap.put(id, dicEntity)
        }
    }

    def getTreeData() {
        dicList.each { dicEntity ->
            addToParentTree(dicEntity)
        }
        return dicTreeMap.root
    }

    def addToParentTree(GenericValue dicEntity) {
        def dicId = dicEntity.getString(idField)
        if (dicTreeMap.containsKey(dicId)) {//当前节点已经构造过了
            return
        }
        def parentId = dicEntity.getString(parentField)
        if (parentId == null) {
            parentId = "root"
        }
        def dicTree = dicTreeMap[parentId]
        if (dicTree == null) {//当前菜单所在的父节点还未构建
            def parentEntity = dicMap[parentId]//获取父节点实体数据
            addToParentTree(parentEntity)
            dicTree = dicTreeMap[parentId]
        }
        def treeChildren = dicTree["children"]
        if (treeChildren == null) {
            treeChildren = []
            dicTree["children"] = treeChildren
        }
        def node = [value: dicEntity.getString(valueField), id: dicEntity.getString(idField)]
        dicTreeMap.put(node.id, node)
        treeChildren.add(node)
    }

}