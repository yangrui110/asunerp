def connection = new URL("http://cl:8080/entityMgr/control/SOAPService/getDataByModelService").openConnection()
connection.setRequestMethod('POST')
connection.setRequestProperty("Content-Type","text/xml")
connection.doOutput=true
def writer = new OutputStreamWriter(connection.outputStream)
def file=new File("A:/NewFile.xml");

writer.println new String(file.readBytes(),"utf-8")
writer.flush()
writer.close()
connection.connect()
def respText = connection.content.text
def returnFile=new File("A:/returnFile.xml");
if(!returnFile.exists()){
	returnFile.createNewFile()
}
returnFile<<respText
