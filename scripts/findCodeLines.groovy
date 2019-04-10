def dir = new File("framework")
def work = new Work()
work.loop(dir)
println(work.line)

class Work {
    def line = 0

    def loop(File file) {
        def fileName = file.name
        if (file.isFile()) {
            if (fileName.endsWith(".xml")
                    || fileName.endsWith(".java")
                    || fileName.endsWith(".ftl")
                    || fileName.endsWith(".html")
                    || fileName.endsWith(".groovy")
                    || fileName == "app.js"
                    || fileName == "app.js"
                    || fileName == "style.css"
            ) {

                def fileLine=0
                file.eachLine {
                    fileLine++
                }
                def msg=fileName+"   $fileLine"
                if(fileLine>1000){
                    System.err.println(msg)
                    System.err.println(file.absolutePath)
                }else{
                    println msg
                }

                line+=fileLine
            }
        } else if(
        fileName != ".idea"
      //  &&fileName != "antlr"
        ) {
            file.eachFile {
                loop(it)
            }
        }
    }
}

