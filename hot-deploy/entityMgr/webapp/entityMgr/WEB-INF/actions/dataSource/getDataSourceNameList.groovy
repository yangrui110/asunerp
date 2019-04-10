import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.entity.Delegator
import com.hanlin.fadp.entity.util.EntityQuery;;

def response=response as HttpServletResponse
response.setContentType("application/json")
def writer=response.writer
def dataSourceNameList= (from("DatabaseSeq") as EntityQuery).select("dataSourceName").queryList()
if(!dataSourceNameList){
	writer.print('''{"error":"请先添加数据源"}''')
}else{
	def content="<select id='dataSourceNameSelect'>"
	dataSourceNameList.each{item->
		content+="<option>${item.dataSourceName}</option>"
	}
	content+="</select>"
	writer.print("""{"content":"$content"}""")
}
return "success"

