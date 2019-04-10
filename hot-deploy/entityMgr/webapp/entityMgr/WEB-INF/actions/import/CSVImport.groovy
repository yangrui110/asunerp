import java.nio.ByteBuffer;

import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.petrescence.datasource.CSVLineReader;
def dataSourceName=parameters.dataSourceName
def entityName=parameters.entityName
def file=parameters.file
def buffer=(ByteBuffer)parameters.file
if(dataSourceName){
	myDelegator=DelegatorFactory.getDelegator(dataSourceName)
	entityMap=myDelegator.getModelEntityMapByGroup(dataSourceName)
	context.entityList=entityMap.keySet().toList()
	if(buffer&&buffer.hasArray()&&entityName){
		def myDelegator=DelegatorFactory.getDelegator(dataSourceName);
		def csv = new String(file.array(),parameters.charSet)
		def lines=csv.split("\n")
		def header=[],i=0
		for(;i<lines.length;i++){
			CSVLineReader lineReader=new CSVLineReader(lines[i]);
			def ok=true
			while(lineReader.hasMoreItem()){
				def item=lineReader.nextItem()
				header<<item
				if(item==null||"".equals(item)){
					ok=false
				}
			}
			if(ok){
				i++
				break
			}else{
				head=[]
			}
		}
		println "header${header}"
		def dataList=[]
		for(;i<lines.length;i++){
			
			CSVLineReader lineReader=new CSVLineReader(lines[i]);
			j=0
			def value=myDelegator.makeValue(entityName)
			for(;lineReader.hasMoreItem();j++){
				def itemValue=lineReader.nextItem()
				if(itemValue!="null"&&itemValue){
//					item[header[j]]=itemValue
					value.setString(header[j], itemValue)
				}
			}
			println '导入数据:'+ value
			if(j!=0){
				dataList<<value
			}
		}
		println dataList
		try{
			b=TransactionUtil.begin(500000)
			dataList.each {
				println "csvIm:${it}"
				myDelegator.create(it)
			}
			TransactionUtil.commit(b)
			context.msg="导入成功"
		}catch(e){
		e.printStackTrace()
			context.msg=e.getMessage()
		}
	}
}
