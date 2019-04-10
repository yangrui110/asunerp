import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.entity.DelegatorFactory
import com.hanlin.fadp.entity.util.EntitySaxReader
import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.io.IOUtils
import org.apache.tools.ant.filters.StringInputStream
import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipFile

import javax.servlet.http.HttpServletResponse

def response = response as HttpServletResponse

def dataSourceName = parameters.dataSourceName
def iDelegator = DelegatorFactory.getDelegator(dataSourceName)
EntitySaxReader entityReader = new EntitySaxReader(iDelegator);

String xmlTxt=parameters.xmlTxt

def returnStr = '{}'
def writer = response.writer


entityReader.parse(new StringInputStream(xmlTxt), null);

writer.print(returnStr)
writer.close()



return "success";
