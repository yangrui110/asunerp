import java.util.*;
import java.util.zip.ZipOutputStream;
import java.io.*;

import com.hanlin.fadp.entity.*;
import com.hanlin.fadp.entity.util.EntityFindOptions;
import com.hanlin.fadp.entity.transaction.*;

import org.apache.tools.zip.*;

import net.sf.json.*;

import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.base.util.*;
import com.hanlin.fadp.entity.DelegatorFactory;



//解析json数据
def store = parameters.store;
def tempPath = "${System.getProperty('user.dir')}\\runtime\\tmp\\";
def tempDownload = ".zip";
def tempdir = null;
def temp = "${System.getProperty('user.dir')}\\runtime\\tmp\\";
def zipName = temp+parameters.myRound+tempDownload

//------------------
InputStream ins = new FileInputStream(new File(zipName));

BufferedInputStream bins = new BufferedInputStream(ins);// 放到缓冲流里面

OutputStream outs = response.getOutputStream();// 获取文件输出IO流

BufferedOutputStream bouts = new BufferedOutputStream(outs);
response.setContentType("application/x-msdownload");// 设置response内容的类型
response.setHeader(
		"Content-disposition",
		"attachment;filename="
		+ URLEncoder.encode(parameters.myRound+tempDownload, "ISO8859-1"));// 设置头部信息
int bytesRead = 0;
byte[] buffer = new byte[8192];
// 开始向网络传输文件流
while ((bytesRead = bins.read(buffer, 0, 8192)) != -1) {
	bouts.write(buffer, 0, bytesRead);
}
bouts.flush();// 这里一定要调用flush()方法
ins.close();
bins.close();
outs.close();
bouts.close();
return "success";