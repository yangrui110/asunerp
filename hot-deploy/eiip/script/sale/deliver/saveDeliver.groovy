import com.eiip.constant.*
import com.hanlin.fadp.base.util.UtilValidate
import com.hanlin.fadp.common.AjaxCURD
import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.common.UtilCodeStr
import com.hanlin.fadp.entity.GenericDelegator


def returnObj = [:]
def delegator = delegator as GenericDelegator
parameters = parameters as Map


def mainEntityName = 'SaleDeliver'
def itemEntityName = 'SaleDeliverItem'


def deliverValue = parameters.deliverValue as Map
def deliverItemList = parameters.deliverItemList
def orderValue = parameters.orderValue as Map
def orderItemList = parameters.orderItemList


// 保存销货单
def mainValue = AjaxCURD.parseValue(delegator, mainEntityName, deliverValue)
def doCreate=UtilValidate.isEmpty(mainValue.deliverId)
if(doCreate){
    // 销货开单
    mainValue.set('deliverCode', UtilCodeStr.getCode(delegator, mainEntityName))
    delegator.createSetNextSeqId(mainValue)
}else{
    // 修改销货单
    mainValue.store()
}


// 保存销货单条目
if (deliverItemList) {
    def list = []
    deliverItemList.each { Map item ->
        item.deliverId = mainValue.deliverId;
        def deliverItemValue=AjaxCURD.parseValue(delegator, itemEntityName, item)
        if (doCreate){
            deliverItemValue.setNextSeqId()
        }
        list<<deliverItemValue
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


