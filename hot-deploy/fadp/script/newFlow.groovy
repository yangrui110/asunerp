import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.workflow.Flow;

def flow =new Flow(delegator,[flowName:"测试流程1",userId:userLogin.userLoginId])
def response =response as HttpServletResponse
response.writer.print flow