import com.hanlin.fadp.base.lang.JSON
import com.hanlin.fadp.entity.DelegatorFactory
import com.hanlin.fadp.entity.util.EntitySaxReader
import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.io.IOUtils
import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipFile

import javax.servlet.http.HttpServletResponse

def response = response as HttpServletResponse

def dataSourceName = parameters.dataSourceName
def iDelegator = DelegatorFactory.getDelegator(dataSourceName)
EntitySaxReader entityReader = new EntitySaxReader(iDelegator);

DiskFileItemFactory diskFactory = new DiskFileItemFactory();
diskFactory.setSizeThreshold(4 * 1024);
File tempDir = new File(System.getProperty("user.dir") + "/runtime/tmp");
if (!tempDir.exists()) {
    tempDir.mkdirs();
}
diskFactory.setRepository(tempDir);
ServletFileUpload upload = new ServletFileUpload(diskFactory);
//		upload.setSizeMax(4 * 1024 * 1024);
List<FileItem> fileItems = upload.parseRequest(request)
FileItem item = fileItems.get(0)
fileItems.each {
    if (it.fieldName == "file") {
        item = it
    }
}


def returnStr = '{}'
def writer = response.writer

if(item.getName().endsWith(".xml")){
    //xml数据导入
    entityReader.parse(item.getInputStream(), null);
}else{
    //zip包中的xml数据导入

    File file = new File(tempDir,item.getName())
    FileOutputStream fout = new FileOutputStream(file);
    IOUtils.copy(item.getInputStream(), fout);
    try {

        ZipFile zip = new ZipFile(file);
        Enumeration<ZipEntry> entries = zip.getEntries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.endsWith(".xml")) {
                // entityReader.setCheckDataOnly(true);

                entityReader.parse(zip.getInputStream(entry), null);


            } else {
                fileEntries.add(entry);

            }
        }
    } catch (e) {
        returnStr = JSON.from([error: e.getMessage()]).toString()
    }

}


writer.print(returnStr)
writer.close()



return "success";
