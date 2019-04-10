
def updateDir=new File('update')
if (updateDir.exists()){
   deleteFile(updateDir)
}
updateDir.mkdir();
updateDir=updateDir.getAbsoluteFile()
[
        'hot-deploy/websdt/build/lib'
        ,'hot-deploy/fadp/build/lib'
      /*  "framework/catalina/fadp-component.xml"
        ,"framework/webapp/config/url.properties"
        ,"fadp.jar"*/
        /*   'hot-deploy/websdt/build/lib/fadp-websdt.jar'
         ,'hot-deploy/websdt/entitydef'
           ,'hot-deploy/websdt/scripts'
           ,'hot-deploy/websdt/servicedef'
           ,'hot-deploy/websdt/template'
           ,'hot-deploy/websdt/webapp'
           ,'hot-deploy/websdt/widget'
           ,'hot-deploy/websdt/build.xml'
           ,'hot-deploy/websdt/fadp-component.xml'*/
].each {

    copyFile(new File(updateDir.parent+'/'+it),new File(updateDir.absolutePath+'/'+it))

}
def copyFile(File srcFile,File desFile){

    if(srcFile.isDirectory()){
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