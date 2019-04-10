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
def ticketId = parameters.ticketId
def snTicketMaterialSnList = EntityQuery.use(delegator).from('StockTransferItemView').where(transferId: ticketId).queryList()
def tempList = []
snTicketMaterialSnList.each { item ->
    def temp = [:]
    tempList << temp
    temp.putAll(item)
    temp.ticketId = item.transferId
    temp.storeNum = 0 - item.transferNum
    temp.storehouseId = item.fromStorehouseId
    temp.batchId = item.fromBatchId

}
snTicketMaterialSnList = tempList
// 修改来源仓库库存
StockUtil.changeStoreNum(delegator, snTicketMaterialSnList)
snTicketMaterialSnList.each { item ->
    item.storeNum = item.transferNum
    item.storehouseId = item.toStorehouseId
    item.batchId = item.toBatchId
}
// 修改目标仓库库存
StockUtil.changeStoreNum(delegator, snTicketMaterialSnList)

// 修改盘点sn状态为入库，并修改仓库、批次
SnUtil.updateMaterialSnByTicket(delegator, snTicketMaterialSnList, SnStatus.inputted)

AjaxUtil.writeJsonToResponse(returnObj, response)
return 'success'

