
import com.hanlin.fadp.common.FindServices;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.model.ModelFieldTypeReader;
import com.hanlin.fadp.service.GenericDispatcher;
def delegator_cus = DelegatorFactory.getDelegator(parameters.myDelegator)
def list=[];
def textTypes=[];
def modelEntity = delegator_cus.getModelEntity(parameters.myEntityName)
def fieldTypeReader = delegator_cus.getModelFieldTypeReader(modelEntity)
fieldTypeReader.getFieldTypes().each {
	if (it.getJavaType()=="String"){
		textTypes<<it.getType()
	}
}
context.textTypes=textTypes
context.dateTypes=['date', 'date-time', 'time']

modelEntity.getFieldsUnmodifiable().each {
	list<<[label:it.getName(),fieldType:it.getType(),fieldValue:'',fieldName:it.getName()]
}
context.list=list





