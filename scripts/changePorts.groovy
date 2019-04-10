/**
 * Created by cl on 2016/12/14.
 */

String rootPath="";
def ports=[http:"8001",https:"8002",admin:"8003",naming:"8004"]
//def ports=[http:"9001",https:"9002",admin:"9003",naming:"9004"]
//def ports=[http:"80",https:"443",admin:"9000",naming:"9001"]
def args=binding.getProperty("args")
if(args){
    ports.http=args[0]
    ports.https=args[1]
    ports.admin=args[2]
    ports.naming=args[3]
}
println("修改为：  $ports")
def files=[fadp_component_xml:rootPath+"framework/catalina/fadp-component.xml"
        ,fadp_component_xml2:rootPath+"framework/base/fadp-component.xml"
           ,url_properties:rootPath+"framework/webapp/config/url.properties"
           ,start_properties:rootPath+"framework/start/src/com/hanlin/fadp/base/start/start.properties"]

pack1={
    def file = new File(files.fadp_component_xml)
// parse out textnodes to modify
    Node root = new XmlParser(false,false).parseText(file.text);
    Node container=root.depthFirst().find{it['@name']=="catalina-container"};

    Node http=container.find{it['@name']=="http-connector"};
    Node port=http.find {it['@name']=="port"}
    port['@value']=ports.http

    Node https=container.find{it['@name']=="https-connector"};
    Node port2=https.find {it['@name']=="port"}
    port2['@value']=ports.https

    def writer =file.newPrintWriter()
    def printer = new XmlNodePrinter(writer);
    printer.print( root );


    println("成功处理http,https："+files.fadp_component_xml)

}


pack2={
    def file = new File(files.fadp_component_xml2)

    Node root = new XmlParser(false,false).parseText(file.text);
    Node container=root.depthFirst().find{it['@name']=="naming-container"};

    Node port=container.find {it['@name']=="port"}
    port['@value']=ports.naming

    def writer =file.newPrintWriter()
    def printer = new XmlNodePrinter(writer);
    printer.print( root );


    println("成功处理naming："+files.fadp_component_xml)

}


pack1()
pack2()





//修改监听端口
modifyPropertiesFile(new File(files.url_properties),["port.https":ports.https,"port.http":ports.http] )
println("成功处理https："+files.url_properties);
modifyPropertiesFile(new File(files.start_properties),["fadp.admin.port":ports.admin] )
println("成功处理http："+files.start_properties);


def modifyPropertiesFile(pFile, Map map) {

    def text = pFile.text
    def pWriter = pFile.newPrintWriter()
    text.eachLine { String textLine ->
        String line = textLine
        map.each { k, v ->
            textLine = textLine.replaceAll(" ","");

            if (textLine.startsWith("$k=")) {
                line = "$k=$v";
            }

        }
        pWriter.println(line)

    }
    pWriter.close()


}