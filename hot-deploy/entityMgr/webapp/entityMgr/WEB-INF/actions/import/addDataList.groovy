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
def module="addDataList.groovy";
def groupResult = [];
def dataSourceName=parameters.dataSourceName
def entityName=parameters.entityName
def file=parameters.file

if(!dataSourceName||!entityName||!file){
	return "error"
}
return ;
	def idelegator=DelegatorFactory.getDelegator(dataSourceName);
	int column = 0;
	Iterator<ModelField> modelFields = idelegator.getModelEntity(entityName).getFieldsIterator();
	while (modelFields.hasNext()) {
		ModelField modelField = modelFields.next();
		
		String fileName = modelField.getName();
		ModelFieldType type = idelegator.getEntityFieldType(idelegator.getModelEntity(entityName), modelField.getType());
		if(modelField.isPk){
			pkFields.add(column);
		}
		fileNames.add(fileName);
		fieldsType.add(type.getJavaType());
		column ++;
	}
	
	beganTransaction = TransactionUtil.begin(3600);
	def col = 0;
	try{
		println valueLine;
		for(def i=0; i<valueLine.size(); i++){
			col++;
			CSVLineReader csvLineReader=new CSVLineReader(valueLine[i]);
			def insertValue=[];
			while (csvLineReader.hasMoreItem()) {
				insertValue.add(csvLineReader.nextCompItem());
			}
			println insertValue
			
			def gv = [:];
			for(int k=0; k<insertValue.size(); k++){
				//按照类型转换
				if(fieldsType[k] == "String"){
					gv.put(fileNames[k], insertValue[k]);
				}else if(fieldsType[k] == "Double"){
					gv.put(fileNames[k], insertValue[k].toDouble());
				}else if(fieldsType[k] == "Long"){
					gv.put(fileNames[k], (Long)insertValue[k].toDouble());
				}else if(fieldsType[k].indexOf("BigDecimal") >= 0){
					gv.put(fileNames[k], insertValue[k].toBigDecimal());
				}else if(fieldsType[k].indexOf("Timestamp") >= 0){
					java.util.Date start_temp;
					java.sql.Timestamp start;
					def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
					start_temp = sdf.parse(insertValue[k]);
					start = new java.sql.Timestamp(start_temp.getTime());
					gv.put(fileNames[k], start);
		
				}else if(fieldsType[k] == "java.sql.Date"){
					java.util.Date start_temp;
					def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
					start_temp = sdf.parse(insertValue[k]);
					gv.put(fileNames[k], start_temp);
				}else if(fieldsType[k] == "java.sql.Time"){
					java.util.Date start_temp;
					java.sql.Time start;
					def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					start_temp = sdf.parse(insertValue[k]);
					start = new java.sql.Time(start_temp.getTime());
					gv.put(fileNames[k], start);
				}else{
					//还没有对int进行处理
				}
			}
			def u = idelegator.create(entityName, gv);
			}
	} catch (Exception e) {
			request.setAttribute("result","导入失败,第"+col+"条数据存在异常");
			context.result="导入失败,第"+col+"条数据存在异常";
		    String errMsg = "====================第"+ col + "条数据存在异常!=========================";
		    Debug.logError(e, errMsg, module);
			return "error";
    try {
        // only rollback the transaction if we started one...
        TransactionUtil.rollback(beganTransaction, errMsg, e);
    } catch (Exception e2) {
        Debug.logError(e2, "Could not rollback transaction: " + e2.toString(), module);
    }
    // after rolling back, rethrow the exception
    throw e;
	} finally {
	    // only commit the transaction if we started one... this will throw an exception if it fails
	    TransactionUtil.commit(beganTransaction);
	}
	request.setAttribute("result","导入成功！");
	context.result="导入成功！";
	return "success";

public class CSVLineReader
{
	private String line;

	private int curPos;

	private int maxPos;

	
	public CSVLineReader(String csvLine)
	{
		line = csvLine;
		curPos = 0;
		maxPos = line.length();
	}

	
	public int getSize()
	{
		int i = 0;
		int size = 1;
		while((i = nextPosition(i)) < maxPos)
		{
			i++;
			size++;
		}
		return size;
	}

	
	public boolean hasMoreItem()
	{
		return (nextPosition(curPos) <= maxPos);
	}

	public String nextItem()
	{
		String item = nextCompItem();
		if(item != null)
		{
			return item.trim();
		}
		return null;
	}

		public String nextCompItem()
	{
	
		if(curPos > maxPos)
		{
			return null;
		}

		int pos = curPos;
		curPos = nextPosition(curPos);

		StringBuffer buffer = new StringBuffer();
		while(pos < curPos)
		{
			char ch = line.charAt(pos);
			pos++;
			if(ch == '"')
			{
				
				if((pos < curPos) && (line.charAt(pos) == '"'))
				{
					buffer.append(ch);
					pos++;
				}
			}
			else
			{
				
				buffer.append(ch);
			}
		}

		
		curPos++;
		return new String(buffer);
	}

	private int nextPosition(int pos)
	{
		boolean isQuote = false;

		while (pos < maxPos)
		{
			char ch = line.charAt(pos);
			if(!isQuote && ch == ',')
			{
				
				break;
			}
			else if('"' == ch)
			{
				
				isQuote = !isQuote;
			}
			pos++;
		}

		return pos;
	}
}
