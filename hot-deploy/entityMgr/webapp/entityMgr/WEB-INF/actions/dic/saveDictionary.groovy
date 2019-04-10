import com.hanlin.fadp.entity.Delegator;


def delegator=(Delegator)delegator
module=parameters.module
value=parameters.value
label=parameters.label
def msg="success"
if(!module||!value||!label){
	msg= "模块,键,值不可为空"
}else{
	try{
		dic= delegator.makeValidValue("Dictionary", parameters)
		delegator.createOrStore(dic)
	}catch(e){
		msg=e.getMessage()
	}
}
response.getWriter().print msg

