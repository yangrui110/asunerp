import com.eiip.constant.SnStatus
import com.eiip.sn.SnUtil
import com.eiip.stock.StockUtil
import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.entity.GenericDelegator
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.condition.EntityOperator
import com.hanlin.fadp.entity.util.EntityQuery

def returnObj = [:]
def delegator = delegator as GenericDelegator
def parameters = parameters as Map
def ticketId=parameters.ticketId
def snTicketMaterialSnList=EntityQuery.use(delegator).from('StockCheckItemView').where(checkId:ticketId).queryList()
def tempList=[]
snTicketMaterialSnList.each { item ->
    def temp=[:]
    tempList<<temp
    temp.putAll(item)
    temp.ticketId=item.checkId
    temp.storeNum=item.diffStockNum

}
snTicketMaterialSnList=tempList

StockUtil.changeStoreNum(delegator, snTicketMaterialSnList)

// 修改盘点sn状态为入库，并修改仓库、批次
SnUtil.updateMaterialSnByTicket(delegator, snTicketMaterialSnList, SnStatus.inputted)

// 对应物料的sn码不属于当前单据，则修改状态为出库
def ticketMaterialSnIdList = []
snTicketMaterialSnList.each { item ->
    ticketMaterialSnIdList << item.ticketMaterialSnId
}
def materialSnValueList = EntityQuery.use(delegator).from('SnTicketSnMapView')
        .where(EntityCondition.makeCondition('ticketMaterialSnId', EntityOperator.IN, ticketMaterialSnIdList))
        .select('materialSnId').queryList()
def materialSnIdList = []
materialSnValueList.each { materialSnIdValue ->
    materialSnIdList << materialSnIdValue.materialSnId
}
def materialSnIdCondition = EntityCondition.makeCondition('materialSnId', EntityOperator.NOT_IN, materialSnIdList)

def materialConditionList = []
snTicketMaterialSnList.each { item ->
    materialConditionList << EntityCondition.makeCondition([materialId: item.materialId, storehouseId: item.storehouseId, batchCode: item.batchCode])
}
def materialCondition = EntityCondition.makeCondition(materialConditionList, EntityOperator.OR)
// 修改对应
delegator.storeByCondition('SnMaterialSn', [snStatus: SnStatus.outputted], EntityCondition.makeCondition(materialSnIdCondition, materialCondition))


AjaxUtil.writeJsonToResponse(returnObj, response)
return 'success'

