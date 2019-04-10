import com.hanlin.fadp.entity.DelegatorFactory
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.condition.EntityOperator
import com.hanlin.fadp.entity.model.ModelEntity
import com.hanlin.fadp.entity.model.ModelRelation
import com.hanlin.fadp.entity.util.EntityQuery
import com.hanlin.fadp.petrescence.datasource.BaseInfo
import org.apache.tools.zip.ZipEntry

import javax.servlet.http.HttpServletResponse
import java.util.zip.ZipOutputStream

def dataSourceName = parameters.dataSourceName
def armyId = parameters.armyId?:null

def iDelegator = DelegatorFactory.getDelegator(dataSourceName)

def rootArmy = EntityQuery.use(iDelegator).from("ArmyOrg").where("armyId", armyId).queryOne()

def armyIdList = [], valueList = []
if (armyId){
    rootArmy.set("parentArmyId",null)
    armyIdList << armyId
    valueList << rootArmy
}

loopFindArmy(iDelegator,["parentArmyId": armyId], armyIdList, valueList)
def orgEntity = iDelegator.getModelEntity("ArmyOrg")
def relationsManyList = orgEntity.relationsManyList
relationsManyList.each { ModelRelation relation ->
    def relEntityName = relation.getRelEntityName()
    def newConn = EntityCondition.makeCondition("armyId", EntityOperator.IN, armyIdList)

    def relEntityList = EntityQuery.use(iDelegator).from(relEntityName).where(newConn).queryList()
    valueList += relEntityList
}
writeValues(response,dataSourceName,valueList)
return "success"

def writeValues(HttpServletResponse response,dataSourceName, valueList) {

    response.setContentType("application/zip")
    response.setHeader("Content-Disposition", "attachment;filename=\"" + dataSourceName + ".zip\"");

    def returnOut = response.getOutputStream()


    ZipOutputStream out = new ZipOutputStream(returnOut)
    out.putNextEntry(new ZipEntry("data.xml"));

    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")))

    writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    writer.println("<entity-engine-xml>");




    valueList.each{item->
        item.writeXmlText(writer, "");
    }


    writer.println("</entity-engine-xml>")
    writer.flush()

    out.closeEntry()
    writer.close()
    out.close()
    returnOut.close()
}

def loopFindArmy(iDelegator,conn, armyIdList, valueList) {
    def list = EntityQuery.use(iDelegator).from("ArmyOrg").where(conn).queryList()
    if (list) {
        def tempArray = []
        list.each { GenericValue value ->
            def childArmyId = value.get("armyId")
            tempArray << childArmyId
            armyIdList << childArmyId
            valueList << value
        }
        def newConn = EntityCondition.makeCondition("parentArmyId", EntityOperator.IN, tempArray)
        loopFindArmy(iDelegator,newConn, armyIdList, valueList)
    }
}
