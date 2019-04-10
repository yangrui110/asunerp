import com.eiip.constant.ApproveStatus
import com.eiip.constant.Flag
import com.hanlin.fadp.base.util.UtilDateTime
import com.hanlin.fadp.base.util.UtilValidate
import com.hanlin.fadp.common.AjaxCURD
import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.entity.GenericDelegator


def returnObj = [:]
def delegator = delegator as GenericDelegator
parameters = parameters as Map
def userLogin = userLogin

// 采购计划条目
def planItemList = parameters.planItemList as List
def planItemDBList = []
planItemList.each { planItem ->
    planItemDBList << AjaxCURD.parseValue(delegator, 'PurchasePlanItem', planItem)
}
delegator.storeAll(planItemDBList)

// 订单、订单条目
saveOrderAndItem(delegator, userLogin, planItemList)


AjaxUtil.writeJsonToResponse(returnObj, response)
return 'success'


def saveOrderAndItem(GenericDelegator delegator, userLogin, planItemList) {
    // 同一个供应商的采购计划条目归为一个订单
    def orderMap = [:]
    def orderItemDBList = []
    planItemList.each { planItem ->
        String providerId = planItem.providerId
        def order = orderMap[providerId]
        if (!order) {
            // 构造订单实体
            def orderId = delegator.getNextSeqId('PurchaseOrder')
            order = [
                    orderId       : orderId,
                    orderCode     : orderId,
                    orderStatus   : ApproveStatus.editing,
                    orderSrc      : 'pos_plan',
                    orderDate     : UtilDateTime.nowDateString('yyyy-MM-dd'),
                    preDeliverDate: UtilDateTime.nowDateString('yyyy-MM-dd'),
                    currency      : planItem.currency,
                    providerId    : planItem.providerId,
                    purchaseStaff : userLogin.userLoginId,
                    totalMoney    : 0
            ]
            orderMap[providerId] = order
        }
        order.totalMoney += planItem.amount
        // 构造订单条目实体
        def orderItem = AjaxCURD.parseValue(delegator, 'PurchaseOrderItem', [
                orderId             : order.orderId,
                materialId          : planItem.materialId,
                planItemId          : planItem.planItemId,
                preDeliverDate      : order.preDeliverDate,
                currency            : planItem.currency,
                price               : planItem.price,
                orderNum            : planItem.orderNum,
                amount              : planItem.amount,
                canArriveNum        : planItem.orderNum,
                arrivedNum          : 0,
                orderItemArrivalFlag: Flag.no,
                canRejectNum        : 0,
                rejectedNum         : 0,
                orderItemRejectFlag : Flag.no,
        ])
        orderItem.setNextSeqId()
        orderItemDBList << orderItem
    }
    def orderDBList = []
    orderMap.each { k, v ->
        orderDBList << AjaxCURD.parseValue(delegator, 'PurchaseOrder', v)
    }
    delegator.storeAll(orderDBList)
    delegator.storeAll(orderItemDBList)

}
