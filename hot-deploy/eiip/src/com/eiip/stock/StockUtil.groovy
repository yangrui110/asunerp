package com.eiip.stock;

import com.hanlin.fadp.common.AjaxCURD;
import com.hanlin.fadp.entity.GenericDelegator;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityOperator;
import com.hanlin.fadp.entity.util.EntityQuery;

import java.util.List;
import java.util.Map;

class StockUtil {

    // 改变库存数量
    static def changeStoreNum(GenericDelegator delegator, List<Map> materialList4Store) {
        def conList = []
        def newStoreList = []
        materialList4Store.each { item ->
            newStoreList << AjaxCURD.parseValue(delegator, 'DataMaterialStore', item)
            conList << EntityCondition.makeCondition([storehouseId: item.storehouseId, materialId: item.materialId, batchId: item.batchId])
        }
        def oldStoreList = EntityQuery.use(delegator).from('DataMaterialStore').where(EntityCondition.makeCondition(conList, EntityOperator.OR)).queryList()
        newStoreList.addAll(oldStoreList)
        newStoreList = combineStoreNum(newStoreList)

        delegator.storeAll(newStoreList)
    }

    // 合并库存数量
    static def combineStoreNum(list) {
        def map = [:]
        list.each { item ->
            def key = getStoreKey(item)
            def oldItem = map[key]
            if (oldItem) {
                // 添加数量
                // TODO：也可能是减少数量
                oldItem.storeNum += item.storeNum
                if (item.storeId) {
                    oldItem.storeId = item.storeId
                }
            } else {
                map[key] = item
            }
        }
        def newList = []
        map.values().each { GenericValue item ->
            if (!item.storeId) {
                item.setNextSeqId()
            }
            newList << item
        }
        return newList
    }

    static def getStoreKey(item) {
        return item.storehouseId + '.' + item.materialId + '.' + item.batchId
    }


}
