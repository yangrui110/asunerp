import com.hanlin.fadp.entity.DelegatorFactory;
if(myDelegator==null||entityName==null||pkFieldName==null||pkFieldValue==null){
	return
}
if(request.getMethod().equalsIgnoreCase("get")){
	try{
		mde=DelegatorFactory.getDelegator(myDelegator)
   }catch(e){
	   return "error"
   }
   def fieldMap= [:]
   if(pkFieldName.class==String.class){
	   fieldMap[pkFieldName]=pkFieldValue
   }else{
   		for (i=0;i<pkFieldName.size();i++) {
			   fieldMap[pkFieldName[i]]=pkFieldValue[i]
		}
   }
   def entity =mde.findOne(entityName, fieldMap, false)
   entity.each {k,v->
	   if(!parameters.containsKey(k)){
		   parameters[k]=v
	   }
   }
}
