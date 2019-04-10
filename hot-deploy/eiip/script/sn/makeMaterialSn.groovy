// 生成SN码
import com.hanlin.fadp.base.util.UtilDateTime
import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.entity.GenericDelegator

def delegator = delegator as GenericDelegator
def parameters = parameters as Map

def genNum = parameters.genNum as Integer
def userLogin = userLogin
def valueList = []
def entityName = 'SnMaterialSn'
def sn, firstSn
def dateTime = UtilDateTime.nowDateString('yyyy-MM-dd HH:mm:ss')
for (int i = 0; i < genNum; i++) {
    // FIXME: 这里需要根据条码生成规则进行生成，暂时使用主键代替
    sn = delegator.getNextSeqId(entityName)
    if (!firstSn) {
        firstSn = sn
    }
    def value = delegator.makeValidValue(entityName, [
            materialSnId: sn + '',
            materialSn  : sn + '',
            genNum      : genNum as Long,
            genTime     : dateTime,
            materialId  : parameters.materialId,
            descCnt     : parameters.descCnt,
            genOperator : userLogin.userLoginId,
            snStatus    : 'sns1',
            exportTimes : 0L,
            batchCode   : firstSn
    ])
    value.materialSn = value.materialSnId;
    valueList << value
}
delegator.storeAll(valueList)
AjaxUtil.writeJsonToResponse([value: [lastSn: sn]], response)
