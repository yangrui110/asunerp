import com.hanlin.fadp.base.util.UtilValidate
import com.hanlin.fadp.common.AjaxCURD
import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.common.UtilCodeStr
import com.hanlin.fadp.entity.GenericDelegator


def returnObj = [:]
def delegator = delegator as GenericDelegator
parameters = parameters as Map


def mainEntityName = 'SaleReject'
def itemEntityName = 'SaleRejectItem'


def rejectValue = parameters.rejectValue as Map
def rejectItemList = parameters.rejectItemList
def orderValue = parameters.orderValue as Map
def orderItemList = parameters.orderItemList


// 保存销货单
def mainValue = AjaxCURD.parseValue(delegator, mainEntityName, rejectValue)
def doCreate=UtilValidate.isEmpty(mainValue.rejectId)
if(doCreate){
    // 销货开单
    mainValue.set('rejectCode', UtilCodeStr.getCode(delegator, mainEntityName))
    delegator.createSetNextSeqId(mainValue)
}else{
    // 修改销货单
    mainValue.store()
}


// 保存销货单条目
if (rejectItemList) {
    def list = []
    rejectItemList.each { Map item ->
        item.rejectId = mainValue.rejectId;
        def rejectItemValue=AjaxCURD.parseValue(delegator, itemEntityName, item)
        if (doCreate){
            rejectItemValue.setNextSeqId()
        }
        list<<rejectItemValue
    }
    delegator.storeAll(list)
}
// 保存销售订单条目
if (orderItemList) {
    def list = []
    orderItemList.each { Map item ->
        list << AjaxCURD.parseValue(delegator, "SaleOrderItem", item)
    }
    delegator.storeAll(list)
}

// 如果所有订单都已开单，则修改订单状态
if (orderValue) {
    // 修改订单状态

    def value = AjaxCURD.parseValue(delegator, "SaleOrder", orderValue)
    value.store()
}
returnObj.value = mainValue
AjaxUtil.writeJsonToResponse(returnObj, response)
return 'success'


