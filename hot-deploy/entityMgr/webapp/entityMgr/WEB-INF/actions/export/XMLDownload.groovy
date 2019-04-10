
import java.util.*;
import java.util.zip.ZipOutputStream;
import java.io.*;

import com.hanlin.fadp.entity.*;
import com.hanlin.fadp.entity.util.EntityFindOptions;
import com.hanlin.fadp.entity.transaction.*;

import org.apache.tools.zip.*;

import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.base.util.*;
import com.hanlin.fadp.entity.DelegatorFactory;
import com.hanlin.fadp.petrescence.datasource.BaseInfo;


println parameters.groupName;

//解析json数据
def store = parameters.store;
def tempPath = "${System.getProperty('user.dir')}\\runtime\\tmp";
def tempDownload = ".zip";
def tempdir = null;
def temp = "${System.getProperty('user.dir')}\\runtime\\tmp\\";
tableNames=parameters.tableNames;
if(tableNames==null){
	return
}else if(tableNames.class==String.class){
	tableNames=[tableNames]
}

def idelegator= DelegatorFactory.getDelegator(parameters.dataSourceName)


if(store.equals("true")){
	//存储文件，在零时的文件夹下	
	tempdir = new File(tempPath);
	if(!tempdir.exists()){
		tempdir.mkdir();
	}
	zipName=temp+parameters.myRound+tempDownload
	println zipName
	ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipName));
	tableNames.each {
		println "导出 :"+it

		try {
			def file=new File(tempdir, "${it}.csv")
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));
			def entity=idelegator.getModelEntity(it)
			def fieldList=BaseInfo.getFieldSetWidthOutTimeStamp(entity);
			writer.println StringUtil.join(fieldList.toList(), ",")
			values = idelegator.findList(it, null, fieldList, null, null, false);
			if(values){
				values.each {value->
					fieldValueList=[]
					fieldList.each {  
						fieldValueList<<value[it]
					}
					writer.println '"'+StringUtil.join(fieldValueList, '","')+'"'
				}
			}
			
//			def file=new File(tempdir, "${it}.xml")
//			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));
//			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//			writer.println("<entity-engine-xml>");
//			def entity=idelegator.getModelEntity(it)
//			def fieldList=entity.getFieldsUnmodifiable();
//			writer.println StringUtil.join(fieldList, ",")
//			
//			values = idelegator.findList(it, null, null, BaseInfo.getFieldSetWidthOutTimeStamp(entity).toList(), null, false);
//			def num = 0;
//			for (item in values) {
//				item.writeXmlText(writer, "");
//				if(parameters.eachCount){
//					num++;
//					if(num >= parameters.eachCount.toInteger())
//						break
//				}
//			}
//			writer.println("</entity-engine-xml>");
			writer.flush()
			writer.close();
			FileInputStream fis = new FileInputStream(file);
			out.putNextEntry(new ZipEntry(file.getName()));
			int len = 0;
			byte[] buffer = new byte[1024];

			while ((len = fis.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}

			out.closeEntry();
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
			error = "导出失败!";
			writer.close();
		}

	}
	out.close();
}
println "***********";

return "success";
