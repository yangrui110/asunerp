import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.entity.Delegator;

def parameters=(Map)parameters
def delegator=(Delegator)delegator
def response=(HttpServletResponse)response
def msg
if(!parameters.module||!parameters.value){
	msg="success"
}else{
	try{
		dic= delegator.makeValidValue("Dictionary", parameters)
		if(delegator.removeValue(dic)>0){
			msg="success"
		}else{
			msg="删除失败"
		}
	}catch(e){
		e.printStackTrace()
		msg=e.getMessage()
	}
}
response.getWriter().print msg
