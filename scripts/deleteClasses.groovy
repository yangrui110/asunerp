
deleteClassFile(new File("framework"))
deleteClassFile(new File("hot-deploy"))
def deleteClassFile(File file){
    if(file.isDirectory()){
        file.listFiles().each {
            deleteClassFile(it)
        }
        file.delete();
    }else{
        if (file.name.endsWith(".class")){
            println "file = $file"
            file.delete()
        }
       
    }
}