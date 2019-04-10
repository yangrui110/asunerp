import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.condition.EntityOperator
import com.hanlin.fadp.entity.util.EntityQuery

import javax.servlet.http.HttpServletResponse

def delegator=delegator as Delegator
def response=response as HttpServletResponse
def entityList = EntityQuery.use(delegator).from("FadpDic").where("isLeaf", "N").orderBy('parentId','orderNum').queryList()
if(entityList){
    def worker = new DicWorker(entityList)
    def jsonStr = JSON.from(worker.treeData).toString()
    println(jsonStr)
    response.getWriter().print(jsonStr)
}


class DicWorker{
    def dicList=null
    def dicMap=[:]
    def dicTreeMap=[root:[id:'root' , value:"字典",children:[]]]

    DicWorker(dicList){
        this.dicList=dicList
        dicList.each {dicEntity->
            def dicId=dicEntity.getString("dicId")
            dicMap.put(dicId,dicEntity)
        }
    }
    def getTreeData(){
        dicList.each {dicEntity->
            addToParentTree(dicEntity)
        }
        return dicTreeMap.root
    }
    def addToParentTree(GenericValue dicEntity){
        def dicId = dicEntity.getString("dicId")
        if(dicTreeMap.containsKey(dicId)){//当前节点已经构造过了
            return
        }
        def parentId = dicEntity.getString("parentId")
        if(parentId==null){
            parentId="root"
        }
        def dicTree = dicTreeMap[parentId]
        if(dicTree==null){//当前菜单所在的父节点还未构建
            def parentEntity=dicMap[parentId]//获取父节点实体数据
            addToParentTree(parentEntity)
            dicTree = dicTreeMap[parentId]
        }
        def treeChildren=dicTree["children"]
        if(treeChildren==null){
            treeChildren=[]
            dicTree["children"]=treeChildren
        }
        def node=[value: dicEntity.getString("dicValue"),id:dicEntity.getString("dicId")]
        node.dicId=node.id
        node.dicValue=node.dicValue
        dicTreeMap.put(node.id,node)
        treeChildren.add(node)
    }

}