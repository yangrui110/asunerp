import com.hanlin.fadp.entity.model.ModelUtil
import groovy.sql.Sql

def sql = Sql.newInstance("jdbc:h2:/d/svn/asunerp/runtime/data/h2/fadp/fadp;AUTO_SERVER=TRUE;TRACE_LEVEL_FILE=0;TRACE_LEVEL_SYSTEM_OUT=0", "fadp", "ofbiz", "org.h2.Driver")
[
//        'SaleOrder',
//        'SaleOrderItem',
//        'SaleDeliver',
//        'SaleDeliverItem',
//        'SaleReject',
//        'SaleRejectItem',
//        'SaleReceive',
//        'SnTicketMaterialSn',
//        'SnTicketSnMap',
//        'StockInput',
//        'StockInputItem',
//        'StockOutput',
//        'StockOutputItem',
//        'StockCheck',
//        'StockCheckItem',
//        'StockTransfer',
//        'StockTransferItem',
        'PurchasePlan',
        'PurchasePlanItem',
        'PurchaseOrder',
        'PurchaseOrderItem',
        'PurchaseArrival',
        'PurchaseArrivalItem',

].each { entityName ->
    sql.execute('delete FROM ' + ModelUtil.javaNameToDbName(entityName) + ';')
}
