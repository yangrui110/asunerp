package com.hanlin.fadp.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.hanlin.fadp.base.util.UtilHttp;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

public class HttpRequestFileDownload {
	public static void download(HttpServletRequest request, HttpServletResponse response, String domain,
			String username, String password, String filePath) throws IOException {
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, username, password);
		if(filePath==null){
			filePath=request.getParameter("filePath");
		}
		if(filePath==null){
			filePath=request.getParameter("filePath");
		}
		SmbFile smbFile = new SmbFile(filePath, auth);

		if (!smbFile.exists()) {
			PrintWriter writer = response.getWriter();
			writer.print("文件不存在");
		} else {
			String mimeType = UtilHttp.getContentTypeByFileName(filePath);
			if (mimeType == null) {
				String type = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
				mimeType = Constant.mimeMap.get(type);
			}
			if (mimeType != null) {
				response.setContentType(mimeType);
			}
			IOUtils.copy(smbFile.getInputStream(), response.getOutputStream());
		}
	}
}
