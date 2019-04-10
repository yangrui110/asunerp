import java.util.regex.Matcher
import java.util.regex.Pattern

totalNum = 0
['system', 'base', 'stock', 'sale', 'purchase', 'produce', 'sn'].each { dir ->
    loopFile(new File("/test/asunerp-www/src/app/" + dir))

}

sb = null

def loopFile(File file) {
    if (file.isDirectory()) {
        file.listFiles().each { childFile ->
            loopFile(childFile)
        }
    } else {
        sb = new StringBuffer()
        def ctx
        def reader = file.newReader()
        def fileChanged = false
        def fileText = file.text
        if (file.name.endsWith('component.html')) {

            def replacementText = '$1<span class="input-group-text">$2</span>$3';


            Matcher myMatcher = fileText =~ /(<label.*input-group-prepend.*>)(.*)(<\/label>)/
            if(myMatcher.find()){
                println "修改文件：$file " + (++totalNum)

                fileText=fileText.replaceAll(/(<label.*input-group-prepend.*>)(.*)(<\/label>)/,replacementText)
                println "$fileText"
                println('====================================================================================================')
                println('====================================================================================================')
                println('====================================================================================================')
                println('====================================================================================================')
                println('====================================================================================================')
                println('====================================================================================================')
                println('====================================================================================================')
            }

        }
        reader.close()

    }


}

def loopNode(Node node) {
    String cl = node['@class']
    if (cl.contains('input-group-prepend')) {
        println(node.toString())
    } else {
        node.children().each {
            loopNode(it)
        }
    }
}

return 'success'