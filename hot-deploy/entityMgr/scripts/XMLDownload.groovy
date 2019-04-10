import com.hanlin.fadp.base.util.StringUtil
import com.hanlin.fadp.entity.DelegatorFactory
import com.hanlin.fadp.petrescence.datasource.BaseInfo
import org.apache.tools.zip.ZipEntry

import javax.servlet.http.HttpServletResponse
import java.util.zip.ZipOutputStream

File tempDir = new File(System.getProperty("user.dir") + "/runtime/tmp");
if (!tempDir.exists()) {
    tempDir.mkdirs();
}
def dataSourceName = parameters.dataSourceName
def maxNum = parameters.maxNum ?: 0

def tableNames = parameters.tableNames;
if (tableNames == null) {
    return
} else if (tableNames.class == String.class) {
    tableNames = [tableNames]
}


def idelegator = DelegatorFactory.getDelegator(dataSourceName)


def response=response as HttpServletResponse
response.setContentType("application/zip")
response.setHeader("Content-Disposition", "attachment;filename=\"" + dataSourceName + ".zip\"");

def returnOut =response.getOutputStream()


ZipOutputStream out = new ZipOutputStream(returnOut)
out.putNextEntry(new ZipEntry("data.xml"));

writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")))

writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
writer.println("<entity-engine-xml>");

tableNames.each { String fileName ->
    println "导出 :" + fileName
    def entity = idelegator.getModelEntity(fileName)
    def fieldList = entity.getFieldsUnmodifiable();

    values = idelegator.findList(fileName, null, null, BaseInfo.getFieldSetWidthOutTimeStamp(entity).toList(), null, false);

    def num = values.size();
    if (maxNum != 0) {
        num = maxNum
    }
    for (int i = 0; i < num; i++) {
        def item = values.get(i)
        item.writeXmlText(writer, "");
    }



}
writer.println("</entity-engine-xml>")
writer.flush()

out.closeEntry()
writer.close()
out.close()
returnOut.close()