package com.hanlin.fadp.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

/**
 * HttpRequestFileUpload - Receive a file upload through an HttpServletRequest
 *
 */
public class HttpRequestFileUpload {
	/**
	 * 上传文件
	 * @param request
	 * @param tempPath
	 * @param savePath
	 * @param domain
	 * @param username
	 * @param password
	 * @return
	 * @throws IOException
	 * @throws FileUploadException
	 */
	public static HttpRequestFileUpload upload(HttpServletRequest request,String tempPath, String savePath,String domain, String username, String password) throws IOException, FileUploadException
	{
		HttpRequestFileUpload upload = new HttpRequestFileUpload();
		upload.tempPath=tempPath;
		upload.savePath=savePath;
		upload.setShareInfo(domain, username, password);
		upload.doUpload(request);
		return upload;
	}

	/*
	 * private int BUFFER_SIZE = 4096; private int WAIT_INTERVAL = 200; // in
	 * milliseconds private int MAX_WAITS = 20; private int waitCount = 0;
	 */
	private NtlmPasswordAuthentication auth;// windows 共享文件夹授权

	public void setShareInfo(String domain, String username, String password) {
		auth = new NtlmPasswordAuthentication(domain, username, password);
	}

	private String tempPath;
	private String savePath;
	private String filename;
	private String contentType;
	private String overrideFilename = null;
	private Map<String, String> fields = new HashMap<>();

	public String getOverrideFilename() {
		return overrideFilename;
	}

	public void setOverrideFilename(String ofName) {
		overrideFilename = ofName;
	}

	public String getFilename() {
		return filename;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getContentType() {
		return contentType;
	}

	public String getFieldValue(String fieldName) {
		if (fields == null || fieldName == null)
			return null;
		return fields.get(fieldName);
	}

	public void doUpload(HttpServletRequest request) throws IOException, FileUploadException {
		DiskFileItemFactory diskFactory = new DiskFileItemFactory();
		diskFactory.setSizeThreshold(4 * 1024);
		File tempDir = new File(tempPath);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		diskFactory.setRepository(tempDir);
		ServletFileUpload upload = new ServletFileUpload(diskFactory);
		upload.setSizeMax(4 * 1024 * 1024);
		List<?> fileItems = upload.parseRequest(request);
		Iterator<?> iter = fileItems.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
			if (item.isFormField()) {
				fields.put(item.getFieldName(), item.getString());
			} else {
				String filename = item.getName();
				filename = filename.substring(filename.lastIndexOf("\\") + 1, filename.length());

				SmbFile savePathFile = new SmbFile(savePath, auth);
				if (!savePathFile.exists()) {
					savePathFile.mkdirs();
				}
				SmbFile uploadFile = new SmbFile(savePath + "/" + filename, auth);
				uploadFile.createNewFile();
				IOUtils.copy(item.getInputStream(), uploadFile.getOutputStream());
				fields.put(item.getFieldName(), uploadFile.getPath());
			}
		}

	}

}
