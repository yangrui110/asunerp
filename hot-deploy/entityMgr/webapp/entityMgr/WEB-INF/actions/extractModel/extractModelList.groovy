import com.hanlin.fadp.common.FindServices;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.service.GenericDispatcher;

def delegator =(Delegator)delegator
def parameters =(Map)parameters
context.entityName="DatabaseModel"
context.inputFields=parameters
context.fieldList=['id','dataSourceName','myEntityName','needSave','description']
if(parameters.pageSize==null){
	parameters.pageSize=10
}else if(parameters.pageSize instanceof String ){
	parameters.pageSize=Integer.parseInt(parameters.pageSize)
}
if(parameters.currPage==null){
	parameters.currPage=1;
}else if(parameters.currPage instanceof String ){
	parameters.currPage=Integer.parseInt(parameters.currPage)
}
context.viewSize=parameters.pageSize
context.viewIndex=(parameters.currPage-1)*parameters.pageSize
def map=FindServices.performFindList(((GenericDispatcher)dispatcher).getDispatchContext(), context);
if(!map.list){
	context.currPage=0
	context.totalPages=0
	context.totalRow=0
}