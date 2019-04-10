import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.entity.DelegatorFactory
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.util.EntityQuery

import javax.servlet.http.HttpServletResponse

def dataSourceName = parameters.dataSourceName
def armyId = parameters.armyId?:null
def condition = [:]
condition.parentArmyId = armyId
def iDelegator = DelegatorFactory.getDelegator(dataSourceName)
def list = EntityQuery.use(iDelegator).from("ArmyOrg").where(condition).queryList()

def response=response as HttpServletResponse
def writer = response.writer
writer.print(JSON.from(list).toString())
writer.close()

return "success"