import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.GenericDelegator
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.condition.EntityOperator
import com.hanlin.fadp.entity.util.EntityQuery

def returnObj = [:]
def delegator = delegator as GenericDelegator
def parameters = parameters as Map

def providerId = parameters.providerId
def statementDate = parameters.statementDate

// 应付款=总应付款-实付款
returnObj.shouldPayMoney = getAllShouldPay(delegator, providerId, statementDate) - getAllPayed(delegator, providerId)
AjaxUtil.writeJsonToResponse(returnObj, response)
return 'success'

// 获取指定截止日期，指定供应商所有应付款
def getAllShouldPay(Delegator delegator, providerId, statementDate) {


    def con1 = EntityCondition.makeCondition('orderDate', EntityOperator.LESS_THAN_EQUAL_TO, statementDate)
    def con2 = EntityCondition.makeCondition('providerId', providerId)
    def con = [con1, con2]

    def statementList = EntityQuery.use(delegator).from('ProviderPurchaseStatementView').where(con).queryList()
    def shouldPayMoney = 0
    if (statementList) {
        statementList.each { statement ->
            shouldPayMoney += statement.amount?:0
        }
    }
    return shouldPayMoney
}

// 获取供应商所有已付款
def getAllPayed(Delegator delegator, providerId) {
    def statementList = EntityQuery.use(delegator).from('PurchaseStatement').where('providerId': providerId).queryList()
    def payedMoney = 0
    if (statementList) {
        statementList.each { statement ->
            payedMoney += statement.payedMoney?:0
        }
    }
    return payedMoney
}