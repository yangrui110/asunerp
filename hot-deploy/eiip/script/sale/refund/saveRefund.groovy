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
def rejectViewValue=EntityQuery.use(delegator).from('SaleRejectView').where('rejectId',editDataTemp.rejectId).queryOne()
editDataTemp.refundedMoneyReject=rejectViewValue.refundedMoney
editDataTemp.totalMoneyReject=rejectViewValue.totalMoney

setDefaultZero(editDataTemp, 'oldRefundedMoney')
setDefaultZero(editDataTemp, 'refundedMoneyReject')
setDefaultZero(editDataTemp, 'totalMoneyReject')

def refundedMoneyReject = editDataTemp.refundedMoneyReject // 销货单实退金额
def totalMoneyReject = editDataTemp.totalMoneyReject // 销货单总额

def shouldRefundMoney = totalMoneyReject-refundedMoneyReject // 应退金额
def refundedMoney = editDataTemp.refundedMoney as double // 实退金额
def oldRefundedMoney = editDataTemp.oldRefundedMoney // 原实退


def addNum = refundedMoney - oldRefundedMoney // 改变值
shouldRefundMoney -= addNum
editDataTemp.refundedMoney = refundedMoney
editDataTemp.shouldRefundMoney = shouldRefundMoney
refundedMoneyReject += addNum

// 保存退款单
def mainValue = AjaxCURD.parseValue(delegator,'SaleRefund', editDataTemp)
def doCreate=UtilValidate.isEmpty(mainValue.refundId)
if (doCreate) {
    mainValue.set('refundCode', UtilCodeStr.getCode(delegator, 'SaleRefund'))
    delegator.createSetNextSeqId(mainValue)
} else {
    mainValue.store()
}

// 修改退货单退款金额 及 销货状态
def rejectValue = AjaxCURD.parseValue(delegator,'SaleReject', [rejectId: editDataTemp.rejectId, refundedMoney: refundedMoneyReject])
if (totalMoneyReject == refundedMoneyReject) {
    rejectValue.rejectStatus = 'rjs4'
}else{
    rejectValue.rejectStatus = 'as3'
}
rejectValue.store()
returnObj.value = mainValue
AjaxUtil.writeJsonToResponse(returnObj, response)
return 'success'


def setDefaultZero(editDataTemp, field) {
    if (UtilValidate.isEmpty(editDataTemp[field])) {
        editDataTemp[field] = 0
    }
}