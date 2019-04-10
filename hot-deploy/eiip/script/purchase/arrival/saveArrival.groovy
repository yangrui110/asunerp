import com.eiip.constant.*
import com.hanlin.fadp.base.util.UtilValidate
import com.hanlin.fadp.common.AjaxCURD
import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.common.UtilCodeStr
import com.hanlin.fadp.entity.GenericDelegator


def returnObj = [:]
def delegator = delegator as GenericDelegator
parameters = parameters as Map


def mainEntityName = 'PurchaseArrival'
def itemEntityName = 'PurchaseArrivalItem'


def arrivalValue = parameters.arrivalValue as Map
def arrivalItemList = parameters.arrivalItemList
def orderValue = parameters.orderValue as Map
def orderItemList = parameters.orderItemList


// 保存到货单
def mainValue = AjaxCURD.parseValue(delegator, mainEntityName, arrivalValue)
def doCreate=UtilValidate.isEmpty(mainValue.arrivalId)
if(doCreate){
    // 到货开单
    mainValue.set('arrivalCode', UtilCodeStr.getCode(delegator, mainEntityName))
    delegator.createSetNextSeqId(mainValue)
}else{
    // 修改到货单
    mainValue.store()
}


// 保存到货单条目
if (arrivalItemList) {
    def list = []
    arrivalItemList.each { Map item ->
        item.arrivalId = mainValue.arrivalId;
        def arrivalItemValue=AjaxCURD.parseValue(delegator, itemEntityName, item)
        if (doCreate){
            arrivalItemValue.setNextSeqId()
        }
        list<<arrivalItemValue
    }
    delegator.storeAll(list)
}
// 保存销售订单条目
if (orderItemList) {
    def list = []
    orderItemList.each { Map item ->
        list << AjaxCURD.parseValue(delegator, "PurchaseOrderItem", item)
    }
    delegator.storeAll(list)
}

// 如果所有订单都已开单，则修改订单状态
if (orderValue) {
    // 修改订单状态

    def value = AjaxCURD.parseValue(delegator, "PurchaseOrder", orderValue)
    value.store()
}
returnObj.value = mainValue
AjaxUtil.writeJsonToResponse(returnObj, response)
return 'success'


