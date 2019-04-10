// 改变SN码状态
// 1. 改变库存数量 ，2.改变sn状态

// FIXME:需要处理SN出入库修改后保存的情况。当前SN出入库不可修改
import com.eiip.sn.SnUtil
import com.eiip.stock.StockUtil
import com.hanlin.fadp.common.AjaxCURD
import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.entity.GenericDelegator
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.condition.EntityCondition
import com.hanlin.fadp.entity.condition.EntityOperator
import com.hanlin.fadp.entity.util.EntityQuery

def returnObj = [:]
def delegator = delegator as GenericDelegator
def parameters = parameters as Map
def snStatus = parameters.snStatus
def materialList4Store = parameters.materialList4Store

StockUtil.changeStoreNum(delegator, materialList4Store)
SnUtil.updateMaterialSnByTicket(delegator, materialList4Store, snStatus)

AjaxUtil.writeJsonToResponse(returnObj, response)
return 'success'
