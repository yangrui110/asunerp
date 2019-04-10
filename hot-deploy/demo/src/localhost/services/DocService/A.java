package localhost.services.DocService;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import localhost.services.ModeDateService.ModeDateService;
import localhost.services.ModeDateService.ModeDateServiceLocator;
import localhost.services.ModeDateService.ModeDateServicePortType;
import weaver.docs.webservices.FileInfo;

public class A {

	public static void main(String[] args) throws RemoteException, MalformedURLException, ServiceException {
		// TODO Auto-generated method stub

		/*
		DocServicePortType docServicePortType=new DocServiceLocator().getDocServiceHttpPort(new URL("http://101.132.38.102:8089/services/DocService"));
        String session=docServicePortType.login("sysadmin","jiyuanoffice&2012#0928",0,"127.0.0.1");
        //String session=docServicePortType.login("service","service1126",0,"127.0.0.1");
        FileInfo[] s=docServicePortType.getFilesByDocs(session,"100");
        for (FileInfo fileInfo : s) {
			System.out.println("path="+fileInfo.getDownloadurl());
		}*/
		addMoidfyModeData();
	}

	/**
	 * 保存（新增、更新）
	 * @throws RemoteException 
	 * @throws ServiceException 
	 * @throws MalformedURLException 
	 */
	
	public static void addMoidfyModeData() throws RemoteException, ServiceException, MalformedURLException{
		ModeDateServicePortType modeDateServiceHttpPort = new ModeDateServiceLocator().getModeDateServiceHttpPort(new URL("http://101.132.38.102:8089/services/ModeDateService"));
		String modeid ="1";
		String dataid="";
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		xml+="<ROOT>";
		xml+="<header>";
		xml+="<userid>507</userid>";//用户id
		xml+="<modeid>"+modeid+"</modeid>";//模块id
		xml+="<id>"+dataid+"</id>";//billid 如果是新增则值为空如果有数据则为修改
		xml +="</header>";
		xml +="<search>";
		xml +="<condition />";
		xml +="<right>Y</right>";//是否验证权限
		xml +="</search>";
		xml +="<data id=\"\">";
		xml +="<maintable>";
		xml +="<field>";
		xml +="<filedname>xm</filedname>";//数据库名称
		xml +="<filedlabel>姓名</filedlabel>";//字段名称
		xml +="<fileddbtype>varchar(500)</fileddbtype>";//数据库类型
		xml +="<filedvalue>"+"yang"+"</filedvalue>";//字段的值
		xml +="<fieldshowname>任务名称</fieldshowname>";
		xml +="</field>";
		xml +="</maintable>";
		xml +="<detail></detail>";
		xml +="</data>";
		xml +="</ROOT>";
		String xmlx="<?xml version=\"1.0\" encoding=\"UTF-8\"?><ROOT><header><userid>507</userid><modeid>1</modeid><id></id></header><search><condition /><right>Y</right></search><data id=\"\"><maintable><field><filedname>mm</filedname><filedlabel></filedlabel><fileddbtype>varchar(500)</fileddbtype><filedvalue>1234</filedvalue><fieldshowname></fieldshowname></field><field><filedname>sj</filedname><filedlabel></filedlabel><fileddbtype>varchar(500)</fileddbtype><filedvalue>15672498534</filedvalue><fieldshowname></fieldshowname></field><field><filedname>xb</filedname><filedlabel></filedlabel><fileddbtype>int</fileddbtype><filedvalue>1</filedvalue><fieldshowname></fieldshowname></field></maintable><detail></detail></data></ROOT>\n";
		System.out.println(modeDateServiceHttpPort.saveModeData(xmlx));
	}
}
