import com.hanlin.fadp.entity.util.EntityFindOptions;
import com.hanlin.fadp.entity.condition.*;
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.base.util.*;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.model.ModelGroupReader;
import com.hanlin.fadp.entity.GenericDelegator;
import java.io.*;
import javax.servlet.http.HttpServletRequest;
import com.hanlin.fadp.entity.transaction.*;
import com.hanlin.fadp.entity.model.ModelFieldType;
import com.hanlin.fadp.entity.GenericDelegator;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelField;
import com.hanlin.fadp.entity.model.ModelReader;
import java.text.SimpleDateFormat;
import java.nio.Buffer;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItem;

def module="CSVImport.groovy";

context.isTrue = "false";
def groupResult = [];


Delegator idelegator;
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


def mysql_entityGroups = null;
def oracle_entityGroups = null;
def entityGroups = null;

if(localhost_mysql_root){
	mysql_entityGroups=localhost_mysql_root.getModelGroupReader().getGroupNames(localhost_mysql_root.getDelegatorBaseName());
}
if(localhost_oracle_root){
	oracle_entityGroups = localhost_oracle_root.getModelGroupReader().getGroupNames(localhost_oracle_root.getDelegatorBaseName());
}
if(delegator){
	entityGroups = delegator.getModelGroupReader().getGroupNames(delegator.getDelegatorBaseName());
}

if(entityGroups){
	entityGroups.each{ groupName->
		groupResult.add(groupName);
	}
}
if(mysql_entityGroups){
	mysql_entityGroups.each{ groupName->
		groupResult.add(groupName);
	}
}
if(oracle_entityGroups){
	oracle_entityGroups.each{ groupName->
		groupResult.add(groupName);
	}
}

//搞到默认com.hanlin的表结构
def entityResults = [];

entityList = delegator.getModelEntityMapByGroup("com.hanlin");

entityList.each{ k, v->
		entityResults.add(k);
}

context.message="数据更新"
context.entityGroups = groupResult;

context.entityResults_first = entityResults;


