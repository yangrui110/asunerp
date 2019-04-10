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
                println('\n')

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

        if (fileChanged) {
            println "修改文件：$file " + (++totalNum)
//            def writer = file.newWriter()
//            writer.write(sb.toString())
//            writer.close()
        }

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

boolean correctNgxComponent(ctx, sb, String line) {
    boolean hasChanged = true
    if (line ==~ /<label(.*)input-group-prepend(.*)<\/label>/) {
        // 模块中引用bootstrap包的替换
        sb.append("""<span class="input-group-text"></span>""")
    } else {
        hasChanged = false
    }
    return hasChanged
}

return 'success'