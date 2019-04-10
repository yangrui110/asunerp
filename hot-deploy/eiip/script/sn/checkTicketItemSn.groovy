// 检查启用SN控制的单据条目是否都已具有SN码
import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.entity.GenericDelegator
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.condition.EntityOperator
import com.hanlin.fadp.entity.util.EntityQuery

def returnObj = [:]
def delegator = delegator as GenericDelegator
def parameters = parameters as Map
def ticketId = parameters.ticketId
def ticketType = parameters.ticketType
def ticketItemIdList = parameters.ticketItemIdList as List


def conList = []
conList << EntityCondition.makeCondition('ticketId': ticketId, 'ticketType': ticketType)
conList << EntityCondition.makeCondition('ticketItemId', EntityOperator.IN, ticketItemIdList)
def dbRowNum = EntityQuery.use(delegator).from('SnTicketMaterialSn').where(conList).queryCount()
if (dbRowNum != ticketItemIdList.size()) {
    returnObj.error = '请完成sn码录入。'
}

AjaxUtil.writeJsonToResponse(returnObj, response)
return 'success'
