import java.nio.ByteBuffer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.bson.Document;

import com.hanlin.fadp.base.util.FileUtil;
import com.hanlin.fadp.base.util.StringUtil;
import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.petrescence.datasource.MongoService;
import com.mongodb.client.FindIterable;

def parameters=(Map)parameters
def request	=(HttpServletRequest)request
def response=(HttpServletResponse)response
def buffer=(ByteBuffer)parameters.file
ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory(10240, FileUtil.getFile("runtime/tmp")));
List<FileItem> uploadedItems = UtilGenerics.<FileItem>checkList(upload.parseRequest(request));
println uploadedItems.get(0).get()
if(uploadedItems!=null){
	println uploadedItems.get(0)
	def collection=MongoService.getCollection(parameters["mongoCollectionName"])
	def head
	def csv = new String(uploadedItems.get(0).get(),"GBK")
	def lines =StringUtil.split(csv, "\r\n");
	def b=false;
	for(i=0;i<lines.size();i++){
		if(b)break
		b=true
		head=[]
		StringUtil.split(lines.get(i),",").each {if(it=="")b=false;else head<<it}
	}
	def list=[]
	for(;i<lines.size();i++){
		docu=new Document()
		values=StringUtil.split(lines.get(i),",")
		for(j=0;j<head.size();j++){
			if(!"null".equalsIgnoreCase(values[j])){
				docu.append(head[j],values[j] )
			}
		}
		list<<docu
	}
	collection.insertMany(list)
	println list
	response.getWriter().print("success")
	return "success"
}