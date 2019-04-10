import com.hanlin.fadp.entity.Delegator;

parameters=(Map)parameters
delegator=(Delegator)delegator
if(request.getMethod().equalsIgnoreCase("get")){
	//表示表单的查看
	return "get"
}
if(parameters.testing=="Y"){
	return "testing"
}else{
	parameters.modelParaList=[]
	if(parameters.paraValue ){
		if(parameters.paraValue instanceof String){
			parameters.modelParaList<<[paraAlias:parameters.paraAlias,paraValue:parameters.paraValue]
		}else{
			for(int i=0;i<parameters.paraValue.size();i++){
				parameters.modelParaList<<[paraAlias:parameters["paraAlias"][i],paraValue:parameters["paraValue"][i]]
			}
		}
	}

	def value=delegator.makeValidValue("ExtractModel",parameters)
	if(parameters.extractModelId){
		delegator.store(value);
	}else{
		value.set("extractModelId",delegator.getNextSeqId("ExtractModel"))
		delegator.create(value)
	}
	return "success"
}
