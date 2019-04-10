import com.hanlin.fadp.base.util.UtilValidate
import com.hanlin.fadp.common.AjaxCURD
import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.common.UtilCodeStr
import com.hanlin.fadp.entity.GenericDelegator
import com.hanlin.fadp.entity.util.EntityQuery


def returnObj = [:]
def delegator = delegator as GenericDelegator
parameters = parameters as Map



def editDataTemp = parameters.editDataTemp as Map
def deliverViewValue=EntityQuery.use(delegator).from('SaleOrder4ReceiveView').where('deliverId',editDataTemp.deliverId).queryOne()
editDataTemp.receivedMoneyOrder=deliverViewValue.receivedMoneyOrder
editDataTemp.totalMoneyOrder=deliverViewValue.totalMoneyOrder
editDataTemp.receivedMoneyDeliver=deliverViewValue.receivedMoney
editDataTemp.totalMoneyDeliver=deliverViewValue.totalMoney

setDefaultZero(editDataTemp, 'oldReceivedMoney')
setDefaultZero(editDataTemp, 'receivedMoneyOrder')
setDefaultZero(editDataTemp, 'totalMoneyOrder')
setDefaultZero(editDataTemp, 'receivedMoneyDeliver')
setDefaultZero(editDataTemp, 'totalMoneyDeliver')

def shouldReceiveMoney = editDataTemp.shouldReceiveMoney as double // 应收金额
def receivedMoney = editDataTemp.receivedMoney as double // 实收金额
def oldReceivedMoney = editDataTemp.oldReceivedMoney // 原实收
def receivedMoneyOrder = editDataTemp.receivedMoneyOrder // 订单实收金额
def totalMoneyOrder = editDataTemp.totalMoneyOrder// 订单总额
def receivedMoneyDeliver = editDataTemp.receivedMoneyDeliver // 销货单实收金额
def totalMoneyDeliver = editDataTemp.totalMoneyDeliver // 销货单总额


def addNum = receivedMoney - oldReceivedMoney // 改变值
shouldReceiveMoney -= addNum
editDataTemp.receivedMoney = receivedMoney
editDataTemp.shouldReceiveMoney = shouldReceiveMoney
receivedMoneyOrder += addNum
receivedMoneyDeliver += addNum

// 保存收款单
def mainValue = AjaxCURD.parseValue(delegator,'SaleReceive', editDataTemp)
def doCreate=UtilValidate.isEmpty(mainValue.receiveId)
if (doCreate) {
    mainValue.set('receiveCode', UtilCodeStr.getCode(delegator, 'SaleReceive'))
    delegator.createSetNextSeqId(mainValue)
} else {
    mainValue.store()
}
// 修改订单收款金额
def orderValue = AjaxCURD.parseValue(delegator,'SaleOrder', [orderId: editDataTemp.orderId, receivedMoney: receivedMoneyOrder])
orderValue.store()
// 修改销货单收款金额 及 销货状态
def deliverValue = AjaxCURD.parseValue(delegator,'SaleDeliver', [deliverId: editDataTemp.deliverId, receivedMoney: receivedMoneyDeliver])
if (totalMoneyDeliver == receivedMoneyDeliver) {
    deliverValue.deliverStatus = 'sls4'
}else{
    deliverValue.deliverStatus = 'as3'
}
deliverValue.store()
returnObj.value = mainValue
AjaxUtil.writeJsonToResponse(returnObj, response)
return 'success'


def setDefaultZero(editDataTemp, field) {
    if (UtilValidate.isEmpty(editDataTemp[field])) {
        editDataTemp[field] = 0
    }
}