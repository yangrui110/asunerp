import com.hanlin.fadp.base.util.UtilDateTime
import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipOutputStream

def zipName='reqTrace'+UtilDateTime.nowDateString("yyyyMMddHHmmss")+'.zip'
ZipOutputStream zipOut=new ZipOutputStream(new File(zipName))


String packDir=System.getProperty("user.dir")+File.separator

[
        "hot-deploy/websdt"
        ,"runtime/data/derby/reqtrace"
        ,"runtime/websdt"

].each {
    addEntry(new File(packDir+it),zipOut,packDir)
}
zipOut.close();

def addEntry(File srcFile,ZipOutputStream zipOut,String root){

    if(srcFile.isDirectory()){
        def dirName=srcFile.getName();
        if(["src",".DS_Store","classes","logs"].contains(dirName)){
            return
        }
        ZipEntry zipEntry = new ZipEntry( srcFile.getAbsolutePath().replace(root,'')+File.separator);
        zipOut.putNextEntry(zipEntry);
        srcFile.listFiles().each {file->
            addEntry(file,zipOut,root)
        }
    }else{
        ZipEntry zipEntry = new ZipEntry( srcFile.getAbsolutePath().replace(root,''));
        zipOut.putNextEntry(zipEntry);
        def srcStream = srcFile.newDataInputStream()
        zipOut << srcStream
        srcStream.close()

        println "压缩完： $srcFile"
    }

}
