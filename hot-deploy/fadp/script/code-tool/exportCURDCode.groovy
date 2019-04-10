import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.base.location.FlexibleLocation
import com.hanlin.fadp.base.util.UtilMisc
import com.hanlin.fadp.base.util.template.FreeMarkerWorker
import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.GenericEntityException
import com.hanlin.fadp.entity.GenericValue
import com.hanlin.fadp.entity.model.ModelEntity
import com.hanlin.fadp.entity.model.ModelUtil
import com.hanlin.fadp.entity.util.EntityQuery
import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipOutputStream

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse
//['operator', 'auditor', 'delOperator', 'operateTime', 'auditTime', 'delTime']
def viewValueFieldSet = ['operator', 'auditor', 'delOperator'] as Set
def delegator = delegator as Delegator
def response = response as HttpServletResponse
def parameters = parameters as Map
def entityName = parameters.entityName
def modelEntity = delegator.getModelEntity(entityName)
def appHeader = modelEntity.getDescription()
if (appHeader == null) {
    appHeader = entityName + "管理"
} else if (appHeader.length() > 10) {
    appHeader = appHeader.substring(0, 10)
}

def entityFieldInfoList = EntityQuery.use(delegator).orderBy('orderNum').where('entityName', entityName).from("EntityFieldInfo").queryList()
newList = []
def component = [
        select         : false,
        trueFalseSelect: false,
        complexSelect  : [],
        imageUpload    : false,
        selectUser     : false,
        selectDept     : false,
        approveState   : false,
]
entityFieldInfoList.each { item ->
    def newItem = [isSelectInput: false, viewValue: false]
    newItem.putAll(item)
    if (modelEntity.getPkFieldNames().contains(item.fieldName)) {
        newItem.PK = 'Y'
    } else {
        newItem.PK = 'N'

    }
    item.each { key, value ->
        if (value == null) {
            newItem[key] = ""
        }
    }

    if (item.component == 'select') {
        component.select = true
        newItem.isSelectInput = true
        newItem.viewValue = true
    }
    if (item.component == 'complexSelect') {
        def mdu = "Select${item.relEntityName}Module"
        if (component.complexSelect.indexOf(mdu) < 0) {
            component.complexSelect << mdu
        }
        newItem.isSelectInput = true
        newItem.viewValue = true
    }
    if (item.component == 'selectDept') {
        component.selectDept = true
        newItem.viewValue = true
    }
    if (item.component == 'selectUser') {
        component.selectUser = true
        newItem.viewValue = true
    }




    if (item.component == 'trueFalseSelect') {
        component.trueFalseSelect = true
        newItem.isSelectInput = true
    }

    if (item.component == 'imageUpload') {
        component.imageUpload = true
    }
    if (item.component == 'currentUser') {
        newItem.viewValue = true
    }

    if (item.isApproveStateField == 'Y') {
        component.approveState = true
        component.viewValue = true

    }

    if (viewValueFieldSet.contains(item.fieldName)) {
        component.viewValue = true
    }


    if (item.isViewValue == 'Y') {
        newItem.viewValue = true
    }else if(item.isViewValue == 'N'){
        // 强制指定该字段不用转义
        newItem.viewValue = false
    }
    newList << newItem
}
if (parameters.entityNameAlias) {
    entityName = parameters.entityNameAlias
}
ftlContext = [
        appHeader      : appHeader,
        entityName     : entityName,
        entityLabel    : modelEntity.getDescription(),
        minusEntityName: ModelUtil.javaNameToDbName(entityName).replaceAll('_', '-').toLowerCase(),
        fieldList      : newList,
        component      : component
]

def templateDir = FlexibleLocation.resolveLocation('component://fadp/script/code-tool/$template').getFile()

response.setContentType("application/zip");
response.setHeader("Content-Disposition", "attachment;filename=\"" + entityName + ".zip\"");
ServletOutputStream outputStream = response.getOutputStream();
zipOut = new ZipOutputStream(outputStream);

loopFile("", new File(templateDir))
zipOut.flush()
zipOut.close()


return 'success'


def loopFile(parentDir, File file) {
    def fileName = file.name.replace('$template', ftlContext.minusEntityName).replace('.ftl', '')
    // 向zip包添加文件
    def newParentDir = parentDir + "/" + fileName
    if (file.isDirectory()) {
        ZipEntry zipEntry = new ZipEntry(newParentDir + "/")

        zipOut.putNextEntry(zipEntry)
        file.listFiles().each { childFile ->
            loopFile(newParentDir, childFile)
        }
    } else if (file.name.endsWith('.ftl')) {
        ZipEntry zipEntry = new ZipEntry(newParentDir)
        zipOut.putNextEntry(zipEntry)
        FreeMarkerWorker.renderTemplateAtLocation(file.toURI().toString(), ftlContext, new OutputStreamWriter(zipOut))
    }
}


