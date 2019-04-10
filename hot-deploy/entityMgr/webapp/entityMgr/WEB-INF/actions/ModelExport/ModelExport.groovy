import com.hanlin.fadp.entity.*;
import com.hanlin.fadp.entity.model.*;

def helperNames=[:];

Delegator localhost_oracle_root = null;
Delegator localhost_mysql_root = null;

try {
	localhost_mysql_root = DelegatorFactory.getDelegator("localhost_mysql_root");
} catch (Exception e) {
	println "==============================================================";
	println "Could not find a delegator with the name localhost_mysql_root";
	println "==============================================================";
	localhost_mysql_root = null;
}

try {
	localhost_oracle_root = DelegatorFactory.getDelegator("localhost_oracle_root");
} catch (Exception e) {
	println "==============================================================";
	println "Could not find a delegator with the name localhost_oracle_root";
	println "==============================================================";
	localhost_oracle_root = null;
}

def entityGroups = null;
def mysql_entityGroups = null;
def oracle_entityGroups = null;
if(delegator){
	entityGroups = delegator.getModelGroupReader().getGroupNames(delegator.getDelegatorBaseName()).iterator();
}
if(localhost_mysql_root){
	mysql_entityGroups=localhost_mysql_root.getModelGroupReader().getGroupNames(localhost_mysql_root.getDelegatorBaseName()).iterator();
}
if(localhost_oracle_root){
	oracle_entityGroups=localhost_oracle_root.getModelGroupReader().getGroupNames(localhost_oracle_root.getDelegatorBaseName()).iterator();
}


//不能直接将iterator直接到context中
if(entityGroups){
	entityGroups.each{ groupName->
		helperNames.put(delegator.getGroupHelperName(groupName),groupName);
	}
}
if(mysql_entityGroups){
	mysql_entityGroups.each{ groupName->
		helperNames.put(localhost_mysql_root.getGroupHelperName(groupName),groupName);
	}
}

if(oracle_entityGroups){
	oracle_entityGroups.each{ groupName->
		helperNames.put(localhost_oracle_root.getGroupHelperName(groupName),groupName);
	}
}


println helperNames;
context.helperNames=helperNames;