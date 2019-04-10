package com.eiip.sn

import com.eiip.constant.SnStatus
import com.hanlin.fadp.base.util.UtilValidate
import com.hanlin.fadp.common.AjaxCURD
import com.hanlin.fadp.entity.GenericDelegator
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.condition.EntityOperator
import com.hanlin.fadp.entity.util.EntityQuery

class SnUtil {

// 检查SN码是否存在
    static def checkSnExist(delegator, returnObj, snList, materialId) {
        def snCondition = snCondition(snList)
        def materialCondition = EntityCondition.makeCondition('materialId', materialId)
        def snSet = snList as Set
        def dbSnList = EntityQuery.use(delegator).from('SnMaterialSn').where([snCondition, materialCondition]).select('materialSnId', 'materialSn').queryList()
        def dbSnSet = [] as Set
        dbSnList.each { item ->
            dbSnSet << item.materialSn
        }
        def notExistSnList = []
        snList.each { item ->
            if (!dbSnSet.contains(item)) {
                notExistSnList << item
            }
        }
        if (notExistSnList.size() > 0) {
            returnObj.error = '不存在sn码: ' + notExistSnList.join(',')
        }
    }

// 检查SN码是否被使用
    static def checkSnDuplicate(delegator, returnObj, snList, ticketId, ticketItemId, ticketType) {
        def conList = []
        def orConList = []
        orConList << EntityCondition.makeCondition('ticketId', EntityOperator.NOT_EQUAL, ticketId)
        orConList << EntityCondition.makeCondition('ticketItemId', EntityOperator.NOT_EQUAL, ticketItemId)
        conList << snCondition(snList)
        conList << EntityCondition.makeCondition('ticketType', ticketType)
        conList << EntityCondition.makeCondition(orConList, EntityOperator.OR)
        def otherTicketUseList = EntityQuery.use(delegator).from('SnTicketSnMap').where(conList).select('materialSn').queryList()
        // 查出数据，即说明当前sn码已被其他单据使用。
        if (otherTicketUseList.size() > 0) {
            def tempList = []
            otherTicketUseList.each { item ->
                tempList << item.materialSn
            }
            returnObj.error = tempList.join(',') + '已被其他单据关联，不允许使用！'
        }
    }

// 检查所退物品是否属于当前订单
    static def checkSnBelongThisDeliver(GenericDelegator delegator, returnObj, List snList, orderId) {
        def deliverIdList = EntityQuery.use(delegator).from('SaleDeliverItem').where('orderId', orderId).select('deliverId').queryList()
        if (UtilValidate.isNotEmpty(deliverIdList)) {
            def deliverIdSet = [] as Set
            deliverIdList.each { GenericValue deliver ->
                deliverIdSet << deliver.deliverId
            }
            def conList = []
            conList << snCondition(snList)
            conList << EntityCondition.makeCondition('ticketId', EntityOperator.IN, deliverIdSet)
            def deliverSnList = EntityQuery.use(delegator).from('SnTicketSnMap').where(conList).select('materialSn').queryList()
            if (deliverSnList.size() != snList.size()) {
                def tempList = []
                def deliverSnSet = [] as Set
                deliverSnList.each { item ->
                    deliverSnSet << item.materialSn
                }
                snList.each { materialSn ->
                    if (!deliverSnSet.contains(materialSn)) {
                        tempList << materialSn
                    }
                }
                returnObj.error = tempList.join(',') + '不是属于当前订单！'
            }
        }

    }


    static def saveTicketAndItem(GenericDelegator delegator, ticketItemObj, List snList, ticketId, ticketItemId) {
        AjaxCURD.save(delegator, [entityName: 'SnTicketMaterialSn', fieldMap: ticketItemObj, autoPK: true])
        // 删除原来的数据
        delegator.removeByCondition('SnTicketSnMap', EntityCondition.makeCondition(['ticketId': ticketId, 'ticketItemId': ticketItemId]))
        def valueList = []
        snList.each { item ->
            def map = [:]
            map.putAll(ticketItemObj)
            map.materialSn = item
            valueList << map
        }
        // 插入新数据
        AjaxCURD.saveAll(delegator, [entityName: 'SnTicketSnMap', valueList: valueList, autoPK: true])
    }

    static def snCondition(snList) {
        return EntityCondition.makeCondition('materialSn', EntityOperator.IN, snList)
    }

    // 改变条码状态、仓库、批次
    static def updateMaterialSnByTicket(GenericDelegator delegator, List<Map> snTicketMaterialSnList, snStatus) {
        // 查询条码与单据条目关联表视图
        def ticketMaterialSnIdList = []
        def map = [:]
        snTicketMaterialSnList.each { item ->
            ticketMaterialSnIdList << item.ticketMaterialSnId
            map[item.ticketMaterialSnId] = item
        }
        def con = EntityCondition.makeCondition('ticketMaterialSnId', EntityOperator.IN,ticketMaterialSnIdList)
        def snTicketMapValueList = EntityQuery.use(delegator).from('SnTicketSnMapView').where(con).select('materialSnId', 'ticketMaterialSnId').queryList()
        // 构造条码需要更新的字段map
        // 构造查询出所有条码的查询条件
        def materialSnList = []
        snTicketMapValueList.each { item ->
            def materialSnValue = delegator.makeValue('SnMaterialSn')
            // 填充条码主键
            materialSnValue.materialSnId = item.materialSnId
            materialSnValue.snStatus = snStatus
            // 从提交的条目中获取sn码需要的：仓库、批次
            def ticket = map[item.ticketMaterialSnId] as Map
            materialSnValue.storehouseId = ticket.storehouseId
//            materialSnValue.batchId = ticket.batchId // FIXME: 这是一个理解误区
            materialSnList << materialSnValue
        }
        // 存储条码
        delegator.storeAll(materialSnList)
    }


    static def getTicketMapKey(item) {
        return item.ticketId + '.' + item.ticketItemId
    }

    // 获取在库sn
    static List<String> getInputtedSnList(GenericDelegator delegator, materialId, batchCode) {
        List<String> returnData = []
        def list = EntityQuery.use(delegator).from('SnMaterialSn')
                .where(materialId: materialId, batchCode: batchCode, snStatus: SnStatus.inputted).queryList()
        if (UtilValidate.isNotEmpty(list)) {
            list.each { item ->
                returnData << item.materialSn
            }
        }
        return returnData
    }

}
