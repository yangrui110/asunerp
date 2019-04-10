def userHome=new File(System.getProperty("user.dir"))


String packPath=userHome.getParent()+"/"+userHome.name+new Date().format("yyyy-MM-dd")
def packDir=new File(packPath)
if (packDir.exists()){
   deleteFile(packDir)
}

packDir.mkdir();
packDir=packDir.getAbsoluteFile()
def arrDir= [

        "framework"
        ,"hot-deploy"
        ,"runtime"
        ,"themes"
        ,"fadp.jar"

]
//arrDir=[
//        "framework/catalina/fadp-component.xml"
//       ,"framework/base/fadp-component.xml"
//       ,"framework/webapp/config/url.properties"
//       ,"framework/start/src/com/hanlin/fadp/base/start/start.properties"
//]
//

arrDir.each {

    copyFile(new File(userHome,it),new File(packDir.absolutePath+'/'+it))

}
copyFile(new File('scripts/install.bat'),new File(packDir,"install.bat"))
copyFile(new File('scripts/finishinstall.bat'),new File(packDir,"finishinstall.bat"))
copyFile(new File('scripts/run.bat'),new File(packDir,"run.bat"))
copyFile(new File('scripts/stop.bat'),new File(packDir,"stop.bat"))
def logsDir=new File(packDir,"runtime/logs")
if(!logsDir.exists()){
    logsDir.mkdirs()
}

Runtime.getRuntime().exec("open "+packPath)

def copyFile(File srcFile,File desFile){

    if(srcFile.isDirectory()){
        def dirName=srcFile.getName();
        if(["src",".DS_Store","classes","logs"].contains(dirName)){
            return
        }
        if(!desFile.exists()){
            desFile.mkdirs()
        }
        srcFile.listFiles().each {file->
            copyFile(file,new File(desFile,file.getName()))
        }
    }else{
        if (!desFile.getParentFile().exists()) {
            desFile.getParentFile().mkdirs()
        }
        def srcStream = srcFile.newDataInputStream()
        def dstStream = desFile.newDataOutputStream()
        dstStream << srcStream
        srcStream.close()
        dstStream.close()
        println "拷贝完： $srcFile"
    }

}

def deleteFile(File file){
    if(file.isDirectory()){
        file.listFiles().each {
            deleteFile(it)
        }
        file.delete();
    }else{
        file.delete();
    }
}