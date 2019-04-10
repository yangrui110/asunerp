import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.util.EntityQuery

import javax.servlet.http.HttpServletResponse

def delegator = delegator as Delegator
def response = response as HttpServletResponse
def entityName = "DeptUserNumView"
def treeIdField = "deptId"
def treeValueField = "deptName"
def entity = delegator.getModelEntity(entityName)
def selectFields = (entity.getAllFieldNames() - entity.getAutomaticFieldNames()) as HashSet
def entityList = EntityQuery.use(delegator).from(entityName).select(selectFields).orderBy("parentId", "orderNum").queryList()
def worker = new Worker(entityList, treeIdField, treeValueField)
def jsonStr = JSON.from(worker.treeData).toString()
response.getWriter().print(jsonStr)


class Worker {
    String treeIdField
    String treeValueField
    def list = null
    def map = [:]
    def parentMap = [:]
    def treeMap = [:]

    Worker(list, treeIdField, treeValueField) {
        this.list = list
        this.treeIdField = treeIdField
        this.treeValueField = treeValueField

        parentMap.put(null,[])
        treeMap.put(null,[:])
        addToParentMap()
    }

    def addToParentMap() {
        list.each { entityValue ->
            def parentId = entityValue.parentId as String
            def id = entityValue[treeIdField] as String
            map[id] = entityValue
            if (!parentId) parentId = null
            def list = parentMap[parentId] as List
            if (!list) {
                list = []
                parentMap[parentId] = list
            }
            list << entityValue
        }
    }

    def getTreeData() {
        parentMap[null].each {GenericValue entityValue ->
            addToParentTree(null, entityValue)
        }
        def rootNode=treeMap[null].children[0]
        countNumTotal(rootNode)
        treeMap.values().each {Map node->
            makeNodeValue(node)
        }
        return rootNode
    }

    def addToParentTree(String parentId, GenericValue entityValue) {
        def id = entityValue.getString(treeIdField)
        def tree = treeMap[parentId]
        def treeChildren = tree["children"]
        if (treeChildren == null) {
            treeChildren = []
            tree["children"] = treeChildren
        }
        def node = [:] + entityValue
        node.name = node.value
        node.id = entityValue.getString(treeIdField)
        treeMap.put(node.id, node)
        treeChildren.add(node)
        parentMap[id].each {GenericValue child ->
            addToParentTree(id, child)
        }
    }

    def countNumTotal(Map node) {
        List children = node.children
        def num = node.get("userNum") ?: 0
        if (children) {
            children.each { Map child ->
                num += countNumTotal(child)
            }
            node.userNum = num
        }
        return num
    }

    def makeNodeValue(Map node) {
        def userNum = node.get("userNum") ?: '0'
        node.value = node.get(treeValueField) + "(${userNum}人)"
    }

}