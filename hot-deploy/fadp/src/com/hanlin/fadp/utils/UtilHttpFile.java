package com.hanlin.fadp.utils;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hanlin.fadp.base.util.*;
import com.hanlin.fadp.common.AjaxCURD;
import com.hanlin.fadp.common.AjaxUtil;
import com.hanlin.fadp.entity.GenericDelegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericPK;
import com.hanlin.fadp.entity.GenericValue;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;


public class UtilHttpFile {


    /**
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws FileUploadException
     */
    public static String uploadFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        GenericDelegator delegator = (GenericDelegator) parameters.get("delegator");
        String applicationName = UtilHttp.getApplicationName(request);
        String fileId = delegator.getNextSeqId("UploadFileSeq");
        int pid = Integer.parseInt(fileId);
        int wNum = pid / 10000;
        int bNum = pid % 10000 / 100;
        int sNum = pid % 100;

        String dir = System.getProperty("fadp.home") + "/runtime/" + applicationName + "/";
        String path = wNum + "/" + bNum + "/" + sNum;

        String filePath = dir + path;
        Map<String, Object> result;
        try {
            result = upload(request, filePath);
            result.put("attachId", fileId);
            result.put("uploadTime", UtilDateTime.nowDateString("yyyy-MM-dd HH:mm:ss"));
            GenericValue userLogin = (GenericValue) parameters.get("userLogin");
            if (userLogin != null) {
                result.put("staffId", userLogin.get("userLoginId"));
                result.put("uploader", userLogin.get("userName"));
            }

        } catch (Exception e) {
            result = UtilMisc.toMap("error", "上传失败");
        }
        AjaxUtil.writeJsonToResponse(result, response);

        return "success";
    }

    public static String removeFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> parameters = UtilHttp.getCombinedMap(request);
        String filePath = (String) parameters.get("filePath");
        Object attachId = parameters.get("attachId");
        GenericDelegator delegator = (GenericDelegator) parameters.get("delegator");
        if (UtilValidate.isNotEmpty(attachId)) {
            //删除数据库数据
            try {
                parameters.put("entityName", "SysAttachments");
                parameters.put("PK", UtilMisc.toList(parameters));
                AjaxCURD.delete(delegator, parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        boolean delete = false;
        if (UtilValidate.isNotEmpty(filePath)) {
            if (filePath.startsWith(UtilHttp.getApplicationName(request))) {//只可删除本应用的文件
                String realFilePath = System.getProperty("fadp.home") + "/runtime/" + filePath;
                delete = new File(realFilePath).delete();
            }
        }
        Map<String, Object> result;
        if (delete) {
            result = UtilMisc.toMap();
        } else {
            result = UtilMisc.toMap("error", "删除失败");
        }
        AjaxUtil.writeJsonToResponse(result, response);
        return "success";
    }


    public static String downloadFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        requestURI = URLDecoder.decode(requestURI, "utf-8");
        String filePath = System.getProperty("fadp.home") + "/runtime/" + requestURI.split("/download/")[1];
        try {
            writeFileToResponse(filePath, response);
        } catch (IOException e) {
            e.printStackTrace();
            PrintWriter writer = response.getWriter();
            writer.print("<h4>文件不存在</h4>");
            writer.close();
        }
        return "success";
    }


    private static Map<String, Object> upload(HttpServletRequest request, String savePath) throws IOException, FileUploadException {

        Map<String, Object> fields = new HashMap<>();
        String homePath = System.getProperty("fadp.home");


        DiskFileItemFactory diskFactory = new DiskFileItemFactory();
        diskFactory.setSizeThreshold(4 * 1024);
        File tempDir = new File(homePath + "/runtime/tempfiles");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        diskFactory.setRepository(tempDir);
        ServletFileUpload upload = new ServletFileUpload(diskFactory);
        List<?> fileItems = upload.parseRequest(request);
        Iterator<?> iter = fileItems.iterator();
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            if (item.isFormField()) {
                fields.put(item.getFieldName(), item.getString("utf-8"));
            } else {
                String filename = item.getName();
                savePath = savePath.replaceAll("\\\\", "/");
                // 判断文件路径是否完整，若不完整则加上fadp.home
                // 兼容linux，windows 文件路径问题：linux文件路径以【/】开头，windows没有
                if (!savePath.startsWith("/")&&homePath.startsWith("/")) {
                    savePath = homePath + "/" + savePath;
                }
                filename = filename.substring(filename.lastIndexOf("/") + 1, filename.length());
                String filePath = savePath + "/" + filename;
                InputStream input;
                OutputStream output;

                File savePathFile = new File(savePath);
                if (!savePathFile.exists()) {
                    savePathFile.mkdirs();
                }
                File uploadFile = new File(filePath);
                uploadFile.createNewFile();
                input = item.getInputStream();
                output = new FileOutputStream(uploadFile);

                int fileSize = IOUtils.copy(input, output);

                fields.put(item.getFieldName(), filename);
                fields.put("fileOriginalName", filename);
                fields.put("filePath", filePath.replace(System.getProperty("fadp.home") + "/runtime/", ""));
                fields.put("fileSize", fileSize);
                fields.put("attachType", UtilHttp.getContentTypeByFileName(filePath));

                input.close();
                output.close();

            }
        }

        return fields;
    }

    private static void writeFileToResponse(String filePath, HttpServletResponse response) throws IOException {
        File file = new File(filePath);

        String mimeType = UtilHttp.getContentTypeByFileName(filePath);
        if (mimeType == null) {
            String fileName = file.getName();
            fileName = new String(fileName.getBytes("gbk"), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            String type = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
            mimeType = Constant.mimeMap.get(type);
        }
        writeFileToResponse(mimeType, new FileInputStream(file), response);
    }

    private static void writeFileToResponse(String mimeType, InputStream inputStream, HttpServletResponse response) throws IOException {
        if (mimeType != null) {
            response.setContentType(mimeType);
        }
        ServletOutputStream outputStream = response.getOutputStream();
        writeFile(inputStream, outputStream);

    }

    private static void writeFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] data = new byte[1024];
        int i = -1;
        while ((i = inputStream.read(data)) > 0) {
            outputStream.write(data, 0, i);
        }
        inputStream.close();
        outputStream.flush();
        outputStream.close();
    }

}
