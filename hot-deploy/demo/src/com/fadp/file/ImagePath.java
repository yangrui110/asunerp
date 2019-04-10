package com.fadp.file;

import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.DelegatorFactory;
import localhost.services.DocService.DocServiceLocator;
import localhost.services.DocService.DocServicePortType;
import weaver.docs.webservices.FileInfo;

import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2018/12/24 10:38
 */
public class ImagePath {


    public static String getPath(String fj) throws RemoteException, MalformedURLException, ServiceException {
             DocServicePortType docServicePortType = new DocServiceLocator().getDocServiceHttpPort(new URL("http://101.132.38.102:8089/services/DocService"));
            String session=docServicePortType.login("service","service1126",0,"127.0.0.1");
            FileInfo[] s=docServicePortType.getFilesByDocs(session,fj);
            if(s.length>0)
                return s[0].getDownloadurl();
            else return "";
    }

    public static Delegator getDelegator(Map<String, Object> attrMap, HttpServletRequest request) {
        String delegatorName = (String) attrMap.get("delegatorName");
        if (UtilValidate.isEmpty(delegatorName)) {
            delegatorName = (String) attrMap.get("dataSourceName");
        }
        if (UtilValidate.isEmpty(delegatorName)) {
            delegatorName = request.getParameter("delegatorName");
        }
        if (UtilValidate.isEmpty(delegatorName)) {
            delegatorName = request.getParameter("dataSourceName");
        }
        if (UtilValidate.isEmpty(delegatorName)) {
            return (Delegator) attrMap.get("delegator");
        }
        return DelegatorFactory.getDelegator(delegatorName);

    }

}
