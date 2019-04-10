import com.hanlin.fadp.entity.GenericDelegator
import com.hanlin.fadp.entity.util.EntityQuery

import javax.servlet.ServletOutputStream

def delegator = delegator as GenericDelegator
def parameters = parameters as Map

def materialId = parameters.materialId
def batchCode = parameters.batchCode

def snValueList = EntityQuery.use(delegator).from('SnMaterialSn')
        .where(batchCode: batchCode, materialId: materialId)
        .select('materialSnId', 'materialSn', 'exportTimes')
        .queryList()
response.setContentType("text/plain")
response.setHeader("Content-Disposition", "attachment;filename=\"" + batchCode + ".txt\"");
ServletOutputStream outputStream = response.getOutputStream();
snValueList.each { item ->
    outputStream.println(item.materialSn)
    item.exportTimes += 1
}
delegator.storeAll(snValueList)
outputStream.flush()
outputStream.close()
return 'success'
