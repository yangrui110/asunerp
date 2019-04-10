import com.hanlin.fadp.entity.Delegator;

def delegator=delegator as Delegator
delegator.removeByAnd("DatabaseModel",[dataModelId:parameters["dataModelId"]])
return "success"