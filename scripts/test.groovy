//def url=new URL("http://localhost:8080/websdt/control/main")
def url=new URL("http://reqtrace.com/websdt/control/main")
//def url=new URL("http://baidu.com")
def connection = url.openConnection()
connection.connect()
Map<String, List<String>> map = connection.getHeaderFields();
// 遍历所有的响应头字段
for (String key : map.keySet()) {
    System.out.println(key + "--->" + map.get(key));
}
def text = connection.getInputStream().getText("UTF-8")
print(text)


"""
null--->[HTTP/1.1 200 OK]
Date--->[Sat, 18 Nov 2017 06:54:47 GMT]
Vary--->[Accept-Encoding]
Transfer-Encoding--->[chunked]
Set-Cookie--->[OFBiz.Visitor=15702; Expires=Sun, 18-Nov-2018 06:54:47 GMT; Path=/, JSESSIONID=08A0A8925FE566CD5A0A1D172C97914E.jvm1; Path=/websdt/; HttpOnly]
Content-Type--->[text/html;charset=UTF-8]
X-Powered-By--->[Servlet/3.0 JSP/2.2 (Apache Tomcat/7.0.64 Java/Oracle Corporation/1.8.0_111-b14)]
Server--->[Apache-Coyote/1.1]

null--->[HTTP/1.1 200 OK]
Date--->[Sun, 19 Nov 2017 03:49:20 GMT]
Vary--->[Accept-Encoding]
Transfer-Encoding--->[chunked]
Set-Cookie--->[OFBiz.Visitor=18413; Expires=Mon, 19-Nov-2018 03:49:19 GMT; Path=/, JSESSIONID=DDC0A861964B740667C6B20E8CEF584E.jvm1; Path=/websdt/; HttpOnly]
Content-Type--->[text/html;charset=UTF-8]
X-Powered-By--->[Servlet/3.0 JSP/2.2 (Apache Tomcat/7.0.64 Java/Oracle Corporation/1.8.0_91-b14)]
Server--->[Apache-Coyote/1.1]


null--->[HTTP/1.1 200 OK]
ETag--->["51-47cf7e6ee8400"]
Date--->[Sat, 18 Nov 2017 06:54:15 GMT]
Content-Length--->[81]
Expires--->[Sun, 19 Nov 2017 06:54:15 GMT]
Last-Modified--->[Tue, 12 Jan 2010 13:48:00 GMT]
Content-Type--->[text/html]
Connection--->[Keep-Alive]
Accept-Ranges--->[bytes]
Server--->[Apache]
Cache-Control--->[max-age=86400]
<html>
<meta http-equiv="refresh" content="0;url=http://www.baidu.com/">
</html>

Process finished with exit code 0

"""

测试提交