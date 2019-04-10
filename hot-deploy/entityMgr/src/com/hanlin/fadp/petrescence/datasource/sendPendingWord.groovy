
def map=[pendingwork:[[:]]]
def xml=
'''<?xml version="1.0" encoding="UTF-8"?>
<pendingWorks>
<!-- 此用户该业务系统待办总数 -->
<totalnumber><![CDATA['''+map.totalnumber+''']]></totalnumber>
<!-- 以下是每条待办数据的相关信息 -->'''

map.pendingwork.each{item->
	xml+='''
	<pendingwork>
		<!-- 待办名称或者待办的描述信息 -->
		<taskName><![CDATA['''+item.taskName+''']]></taskName>
		<!-- 待办发送人的中文名称-->
		<appSendUID><![CDATA['''+item.appSendUID+''']]></appSendUID>
		<!-- 该业务系统待办类型，如：公司发文，项目流程，出差申请等 -->
		<taskType><![CDATA['''+item.taskType+''']]></taskType>
		<!-- 待办接收时间 格式必须是yyyy-mm-dd 24hh:MM:ss-->
		<sendTime><![CDATA['''+item.sendTime+''']]></sendTime>
		<!-- 待办超期时间 格式必须是yyyy-mm-dd 24hh:MM:ss-->
		< endTime><![CDATA['''+item.endTime+''']]></endTime>
		<!-- 待办紧急程度，必填越小越紧急,0:特急 1:紧急 2:一般,缺省选2 -->
		<priority><![CDATA['''+item.priority+''']]></priority>
		<!-- 待办单点访问URL地址，必须可以实现单点登录，并且要能直接打开待办的处理页面 这个URL地址为:http://tam.701.com/loginApp/LoginProject/SMOALogin.jsp?url=后面加上待办地址，如果有&符号将&改为@amp;-->
		<url><![CDATA['''+item.url+''']]></url>
		<!-- 待办的描述信息，如果没有则可以为待办完整名称 -->
		<taskDesc><![CDATA['''+item.taskDesc+''']]></taskDesc>
	</pendingwork>
'''
}
xml+='''</pendingWorks>'''

println xml
