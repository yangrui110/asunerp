/*
` * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import java.text.*;
import java.util.Date;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.*;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.exception.entity.base.GenericEntityException;
import com.hanlin.fadp.security.Security;
import com.hanlin.fadp.entity.model.ModelReader;
import com.hanlin.fadp.entity.model.ModelEntity;
import com.hanlin.fadp.entity.model.ModelViewEntity;
import com.hanlin.fadp.entity.model.ModelViewEntity.ModelAlias;
import com.hanlin.fadp.entity.model.ModelField;
import com.hanlin.fadp.entity.model.ModelFieldType;
import com.hanlin.fadp.entity.GenericEntity;
import com.hanlin.fadp.base.util.UtilFormatOut;
import com.hanlin.fadp.base.util.UtilProperties;
import com.hanlin.fadp.entity.condition.EntityExpr;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityConditionList;
import com.hanlin.fadp.entity.condition.EntityFieldMap;
import com.hanlin.fadp.entity.condition.EntityOperator;
import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.entity.util.EntityFindOptions;
import com.hanlin.fadp.entity.util.EntityListIterator;
import com.hanlin.fadp.base.util.Debug;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;
import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

entityName = parameters.entityName;

println "#####################";
def mathBigDecimal = [];
def mathDouble = [];
def mathLong = [];

def sqlDate = [];
def sqlTime = [];
def sqlTimestamp = [];

parameters.each {k, v->
	
	//对mathBigDecimal的处理
	if(k.indexOf("startBigDecimal_") > -1){
		mathBigDecimal.add(k.substring(16, k.length()));
	}
	
	//对mathDouble的处理
	if(k.indexOf("startDouble_") > -1){
		mathDouble.add(k.substring(12, k.length()));
	}
	
	//对mathLong的处理
	if(k.indexOf("startLong_") > -1){
		mathLong.add(k.substring(10, k.length()));
	}
	
	//对sqlTime的处理
	if(k.indexOf("startTime_") > -1){
		sqlTime.add(k.substring(10, k.length()));
	}
	
	//对sqlDate的处理
	if(k.indexOf("startDate_") > -1){
		sqlDate.add(k.substring(10, k.length()));
	}
	
	//对sqlTimestamp的处理
	if(k.indexOf("startTimestamp_") > -1){
		sqlTimestamp.add(k.substring(15, k.length()))
	}
}




//println entityName;
prntln  "============================"
prntln  "============================"





ModelReader reader = delegator.getModelReader();
ModelEntity modelEntity = reader.getModelEntity(entityName);

//ModelEntity --> 表结构

groupByFields = FastList.newInstance();
functionFields = FastList.newInstance();

if (modelEntity instanceof ModelViewEntity) {
    aliases = modelEntity.getAliasesCopy()
    for (ModelAlias alias : aliases) {
        if (alias.getGroupBy()) {
            groupByFields.add(alias.getName());
        } else if (alias.getFunction()) {
            functionFields.add(alias.getName());
        }
    }
}

context.entityName = modelEntity.getEntityName();
context.plainTableName = modelEntity.getPlainTableName();

String hasViewPermission = (security.hasEntityPermission("ENTITY_DATA", "_VIEW", session) || security.hasEntityPermission(modelEntity.getPlainTableName(), "_VIEW", session)) == true ? "Y" : "N";
String hasCreatePermission = (security.hasEntityPermission("ENTITY_DATA", "_CREATE", session) || security.hasEntityPermission(modelEntity.getPlainTableName(), "_CREATE", session)) == true ? "Y" : "N";
String hasUpdatePermission = (security.hasEntityPermission("ENTITY_DATA", "_UPDATE", session) || security.hasEntityPermission(modelEntity.getPlainTableName(), "_UPDATE", session)) == true ? "Y" : "N";
String hasDeletePermission = (security.hasEntityPermission("ENTITY_DATA", "_DELETE", session) || security.hasEntityPermission(modelEntity.getPlainTableName(), "_DELETE", session)) == true ? "Y" : "N";

context.hasViewPermission = hasViewPermission;
context.hasCreatePermission = hasCreatePermission;
context.hasUpdatePermission = hasUpdatePermission;
context.hasDeletePermission = hasDeletePermission;

String find = true;

String curFindString = "entityName=" + entityName + "&find=" + find;

GenericEntity findByEntity = delegator.makeValue(entityName);
List errMsgList = FastList.newInstance();
Iterator fieldIterator = modelEntity.getFieldsIterator();


//--------------------------------

while (fieldIterator.hasNext()) {
    ModelField field = fieldIterator.next();
    String fval = parameters.get(field.getName());
//	println "---------------------------"
//	println field;
//	println fval;
    if (fval != null) {
        if (fval.length() > 0) {
            curFindString = curFindString + "&" + field.getName() + "=" + fval;
            try {
                findByEntity.setString(field.getName(), fval);
            } catch (NumberFormatException nfe) {
                Debug.logError(nfe, "Caught an exception : " + nfe.toString(), "FindGeneric.groovy");
                errMsgList.add("Entered value is non-numeric for numeric field: " + field.getName());
            }
        }
    }
}
if (errMsgList) {
    request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
}

curFindString = UtilFormatOut.encodeQuery(curFindString);
context.curFindString = curFindString;

try {
    viewIndex = Integer.valueOf((String)parameters.get("VIEW_INDEX")).intValue();
} catch (NumberFormatException nfe) {
    viewIndex = 0;
}

context.viewIndexFirst = 0;
context.viewIndex = viewIndex;
context.viewIndexPrevious = viewIndex-1;
context.viewIndexNext = viewIndex+1;

try {
    viewSize = Integer.valueOf((String)parameters.get("VIEW_SIZE")).intValue();
} catch (NumberFormatException nfe) {
    viewSize = Integer.valueOf(UtilProperties.getPropertyValue("widget.properties", "widget.form.defaultViewSize")).intValue();
}

context.viewSize = viewSize;

int lowIndex = viewIndex*viewSize+1;
int highIndex = (viewIndex+1)*viewSize;
context.lowIndex = lowIndex;

int arraySize = 0;
List resultPartialList = null;

if ("true".equals(find)) {
    //EntityCondition condition = EntityCondition.makeCondition(findByEntity, EntityOperator.AND);

    // small variation to support LIKE if a wildcard (%) is found in a String
    conditionList = FastList.newInstance();
    findByKeySet = findByEntity.keySet();
    fbksIter = findByKeySet.iterator();
	//每个表的最后四个字段没有用
	
	//Double的处理
	if(mathDouble.size() > 0){
		for(it in mathDouble){
			
			//开始对数值区间的处理
			String start = request.getParameter("startDouble_" + it);
			String end = request.getParameter("endDouble_" + it);
			if(start == null){
				start = "";
			}
			if(end == null){
				end = "";
			}
			start = start.trim();
			end = end.trim();

			if(start != "" && end != "")
			{
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.LESS_THAN_EQUAL_TO, end.toDouble()));
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.GREATER_THAN_EQUAL_TO, start.toDouble()));
			}
			if(start == "" &&end != "")
			{
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.LESS_THAN_EQUAL_TO, end.toDouble()));
			}
			if(start != "" && end == "")
			{
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.GREATER_THAN_EQUAL_TO, start.toDouble()));
			}
			
		}
		
	}
	
	//对Long的处理
	if(mathLong.size() > 0){
		for(it in mathLong){
			
			//开始对数值区间的处理
			String start = request.getParameter("startLong_" + it);
			String end = request.getParameter("endLong_" + it);
			println start + ", " + end;
			if(start == null){
				start = "";
			}
			if(end == null){
				end = "";
			}
			start = start.trim();
			end = end.trim();

			if(start != "" && end != "")
			{
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.LESS_THAN_EQUAL_TO, (Long)end.toDouble()));
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.GREATER_THAN_EQUAL_TO, (Long)start.toDouble()));
			}
			if(start == "" &&end != "")
			{
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.LESS_THAN_EQUAL_TO, (Long)end.toDouble()));
			}
			if(start != "" && end == "")
			{
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.GREATER_THAN_EQUAL_TO, (Long)start.toDouble()));
			}
			
		}
		
	}
	
	println "------------------------";
	println mathBigDecimal.size();
	
	//对BigDecimal的处理
	if(mathBigDecimal.size() > 0){
		for(it in mathBigDecimal){
			
			//开始对数值区间的处理
			String start = request.getParameter("startBigDecimal_" + it);
			String end = request.getParameter("endBigDecimal_" + it);
			println "====" + start + ", " + end;
			if(start == null){
				start = "";
			}
			if(end == null){
				end = "";
			}
			start = start.trim();
			end = end.trim();

			if(start != "" && end != "")
			{
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.LESS_THAN_EQUAL_TO, end.toBigDecimal()));
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.GREATER_THAN_EQUAL_TO, start.toBigDecimal()));
			}
			if(start == "" &&end != "")
			{
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.LESS_THAN_EQUAL_TO, end.toBigDecimal()));
			}
			if(start != "" && end == "")
			{
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.GREATER_THAN_EQUAL_TO, start.toBigDecimal()));
			}
			
		}
		
	}
	
	
	
	//对日期区间的处理
	
	//还没有针对不同的时间进行处理
	//先处理sqldate
	if(sqlDate.size() > 0){
		for(it in sqlDate){
			
			//保存最后时间的变量
			java.util.Date start_temp;
			java.util.Date end_temp;
			
			java.sql.Date start;
			java.sql.Date end;
			
			//开始对时间区间的处理
			String start_date = request.getParameter("startDate_" + it);
			String end_date = request.getParameter("endDate_" + it);
			
			
			def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			
			
			
			
			if(start_date != null && start_date != "")
			{
				
				start_temp = sdf.parse(start_date);
				start = new java.sql.Date(start_temp.getTime()); 
			}
			if(end_date != null && end_date != "")
			{
				
				end_temp = sdf.parse(end_date);
				end = new java.sql.Date(end_temp.getTime());
			}
			
			 

			if(start != null && end != null)
			{
				
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.LESS_THAN_EQUAL_TO, end));
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.GREATER_THAN_EQUAL_TO, start));
			}
			if(start == null &&end != null)
			{	
				
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.LESS_THAN_EQUAL_TO, end));
			}
			if(start != null && end == null)
			{	
				
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.GREATER_THAN_EQUAL_TO, start));
			}
			
		}
		
	}
	println "`````````````" + sqlDate.size();
	println "assssssssssss" + sqlTime.size();
	//对sqlTime的处理
	if(sqlTime.size() > 0){
		for(it in sqlTime){
			
			//保存最后时间的变量
			java.util.Date start_temp;
			java.util.Date end_temp;
			
			java.sql.Time start;
			java.sql.Time end;
			
			//开始对时间区间的处理
			String start_date = request.getParameter("startTime_" + it);
			String end_date = request.getParameter("endTime_" + it);
			
			
			def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			if(start_date != null && start_date != "")
			{
				
				start_temp = sdf.parse(start_date);
				start = new java.sql.Time(start_temp.getTime());
			}
			if(end_date != null && end_date != "")
			{
				
				end_temp = sdf.parse(end_date);
				end = new java.sql.Time(end_temp.getTime());
			}
			
			 

			if(start != null && end != null)
			{
				
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.LESS_THAN_EQUAL_TO, end));
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.GREATER_THAN_EQUAL_TO, start));
			}
			if(start == null &&end != null)
			{
				
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.LESS_THAN_EQUAL_TO, end));
			}
			if(start != null && end == null)
			{
				
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.GREATER_THAN_EQUAL_TO, start));
			}
			
		}
		
	}
	
	//对sqlTimestamp的处理
	println sqlTimestamp.size();
	if(sqlTimestamp.size() > 0){
		for(it in sqlTimestamp){
			
			//保存最后时间的变量
			java.util.Date start_temp;
			java.util.Date end_temp;
			
			java.sql.Timestamp start;
			java.sql.Timestamp end;
			
			//开始对时间区间的处理
			String start_date = request.getParameter("startTimestamp_" + it);
			String end_date = request.getParameter("endTimestamp_" + it);
			
			
			
			
			def sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

			if(start_date != null && start_date != "")
			{
				
				start_temp = sdf.parse(start_date);
				start = new java.sql.Timestamp(start_temp.getTime());
			}
			if(end_date != null && end_date != "")
			{
				
				end_temp = sdf.parse(end_date);
				end = new java.sql.Timestamp(end_temp.getTime());
			}
			
			 println start.toString() + ", " + end.toString();

			if(start != null && end != null)
			{
				
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.LESS_THAN_EQUAL_TO, end));
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.GREATER_THAN_EQUAL_TO, start));
			}
			if(start == null &&end != null)
			{
				
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.LESS_THAN_EQUAL_TO, end));
			}
			if(start != null && end == null)
			{
				
				conditionList.add(EntityCondition.makeCondition(it, EntityOperator.GREATER_THAN_EQUAL_TO, start));
			}
			
		}
		
	}
	
	
	
    while (fbksIter.hasNext()) {
        findByKey = fbksIter.next();
	//	println findByKey + ", " + findByEntity.getString(findByKey);
        if (findByEntity.getString(findByKey).indexOf("%") >= 0) {
		
            conditionList.add(EntityCondition.makeCondition(findByKey, EntityOperator.LIKE, findByEntity.getString(findByKey)));
        } else{
			
			conditionList.add(EntityCondition.makeCondition(findByKey, EntityOperator.EQUALS, findByEntity.get(findByKey)));
            
        }
    }
	
	
    condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

    if ((highIndex - lowIndex + 1) > 0) {
        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();

            EntityFindOptions efo = new EntityFindOptions();
            efo.setMaxRows(highIndex);
            efo.setResultSetType(EntityFindOptions.TYPE_SCROLL_INSENSITIVE);
            EntityListIterator resultEli = null;
            fieldsToSelect = null;

            if (groupByFields || functionFields) {
                fieldsToSelect = FastSet.newInstance();

                for (String groupByField : groupByFields) {
                    fieldsToSelect.add(groupByField);
                }

                for (String functionField : functionFields) {
                    fieldsToSelect.add(functionField)
                }
            }

			println "###################";
			println entityName + ", " + condition;
			
            resultEli = delegator.find(entityName, condition, null, fieldsToSelect, null, efo);
            resultPartialList = resultEli.getPartialList(lowIndex, highIndex - lowIndex + 1);
			
			println "---------------";
			println resultEli;

            arraySize = resultEli.getResultsSizeAfterPartialList();
            if (arraySize < highIndex) {
                highIndex = arraySize;
            }

            resultEli.close();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Failure in operation, rolling back transaction", "FindGeneric.groovy");
            try {
                // only rollback the transaction if we started one...
                TransactionUtil.rollback(beganTransaction, "Error looking up entity values in WebTools Entity Data Maintenance", e);
            } catch (GenericEntityException e2) {
                Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), "FindGeneric.groovy");
            }
            // after rolling back, rethrow the exception
            throw e;
        } finally {
            // only commit the transaction if we started one... this will throw an exception if it fails
            TransactionUtil.commit(beganTransaction);
        }
    }
}
context.highIndex = highIndex;
context.arraySize = arraySize;
context.resultPartialList = resultPartialList;

viewIndexLast = UtilMisc.getViewLastIndex(arraySize, viewSize);
context.viewIndexLast = viewIndexLast;

List fieldList = FastList.newInstance();
fieldIterator = modelEntity.getFieldsIterator();
while (fieldIterator.hasNext()) {
    ModelField field = fieldIterator.next();
    ModelFieldType type = delegator.getEntityFieldType(modelEntity, field.getType());

    Map fieldMap = FastMap.newInstance();
    fieldMap.put("name", field.getName());
    fieldMap.put("isPk", (field.getIsPk() == true) ? "Y" : "N");
    fieldMap.put("javaType", type.getJavaType());
    fieldMap.put("sqlType", type.getSqlType());
    fieldMap.put("param", (parameters.get(field.getName()) != null ? parameters.get(field.getName()) : ""));

    fieldList.add(fieldMap);
}
context.fieldList = fieldList;
context.columnCount = fieldList.size()+2;

List records = FastList.newInstance();
if (resultPartialList != null) {
    Iterator resultPartialIter = resultPartialList.iterator();
    while (resultPartialIter.hasNext()) {
        Map record = FastMap.newInstance();

        GenericValue value = (GenericValue)resultPartialIter.next();
        String findString = "entityName=" + entityName;
        Iterator pkIterator = modelEntity.getPksIterator();
        while (pkIterator.hasNext()) {
            ModelField pkField = pkIterator.next();
            ModelFieldType type = delegator.getEntityFieldType(modelEntity, pkField.getType());
            findString += "&" + pkField.getName() + "=" + value.get(pkField.getName());
        }
        record.put("findString", findString);

        record.put("fields", value);
        records.add(record);
    }
}
println "*************************";
println records;
context.records = records;
context.lowCount = lowIndex;
context.highCount = lowIndex + records.size() - 1;
context.total = arraySize;
