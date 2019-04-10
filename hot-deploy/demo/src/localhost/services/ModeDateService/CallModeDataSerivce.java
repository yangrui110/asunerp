package localhost.services.ModeDateService;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;


public class CallModeDataSerivce {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws ServiceException, RemoteException {
		// TODO Auto-generated method stub
		CallModeDataSerivce c = new CallModeDataSerivce();
		c.getAllModeDataList();
	}
	
	/**
	 * 保存（新增、更新）
	 */
	public void addMoidfyModeData() throws ServiceException, RemoteException {
		ModeDateService modeDateService = new ModeDateServiceLocator();
		ModeDateServicePortType client= modeDateService.getModeDateServiceHttpPort();
		String modeid = "127";
		String khmc  ="测试客户1231111";
		String dataid = "1";
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		xml +="<ROOT>";
		xml +="<header>";
		xml +="<userid>1</userid>";//用户id
		xml +="<modeid>"+modeid+"</modeid>";//模块id
		xml +="<id>"+dataid+"</id>";//billid 如果是新增则值为空   如果有数据则为修改
		xml +="</header>";
		xml +="<search>";
		xml +="<condition />";
		xml +="<right>Y</right>";//是否验证权限
		xml +="</search>";
		xml +="<data id=\"\">";
		xml +="<maintable>";
		xml +="<field>";
		xml +="<filedname>name</filedname>";//数据库名称
		xml +="<filedlabel>名称</filedlabel>";//字段名称
		xml +="<fileddbtype>varchar(256)</fileddbtype>";//数据库类型
		xml +="<filedvalue>"+khmc+"</filedvalue>";//字段的值
		xml +="<fieldshowname>名称</fieldshowname>";
		xml +="</field>";
		xml +="<files>";
		xml +="<filedname>file1</filedname>";//数据库名称
		xml +="<file>";
		xml +="<filename>w.doc</filename>";//附件名字
		xml +="<filecontent>http://www.badiu.com/1.zip</filecontent>";//附件url
		xml +="</file>";
		xml +="<file>";
		xml +="<filename>w2.doc</filename>";//附件名字
		xml +="<filecontent>http://www.badiu.com/2.zip</filecontent>";//附件url
		xml +="</file>";
		xml +="</files>";
		xml +="</maintable>";
		xml +="<detail>";
		xml +="<detailtable id='0'>";//id为明细表序号  第一个从0开始
		/* row元素
		 * action有3个选项  1.add新增  2.update修改 3.delete删除
		 * id为数据的id字段的值  当action为 update/delete时有效
		 * */
		xml +="<row id='1' action='add'>";
		xml +="<field>";
		xml +="<filedname>no</filedname>";//数据库名称
		xml +="<filedlabel>编号</filedlabel>";//字段名称
		xml +="<fileddbtype>varchar(256)</fileddbtype>";//数据库类型
		xml +="<filedvalue>"+khmc+"</filedvalue>";//字段的值
		xml +="<fieldshowname>编号</fieldshowname>";
		xml +="</field>";
		xml +="<field>";
		xml +="<filedname>name</filedname>";//数据库名称
		xml +="<filedlabel>名称</filedlabel>";//字段名称
		xml +="<fileddbtype>varchar(256)</fileddbtype>";//数据库类型
		xml +="<filedvalue>"+khmc+"</filedvalue>";//字段的值
		xml +="<fieldshowname>名称</fieldshowname>";
		xml +="</field>";
		xml +="<files>";
		xml +="<filedname>file1</filedname>";//数据库名称
		xml +="<file>";
		xml +="<filename>w.doc</filename>";//附件名字
		xml +="<filecontent>http://www.badiu.com/1.zip</filecontent>";//附件url
		xml +="</file>";
		xml +="<file>";
		xml +="<filename>w2.doc</filename>";//附件名字
		xml +="<filecontent>http://www.badiu.com/2.zip</filecontent>";//附件url
		xml +="</file>";
		xml +="</files>";
		xml +="</row>";
		xml +="</detailtable>";
		xml +="</detail>";
		xml +="</data>";
		xml +="</ROOT>";
		System.out.println(client.saveModeData(xml));
	}
	
	/**
	 * 删除表单数据
	 */
	public void deleteModeDataService() throws ServiceException, RemoteException {
		ModeDateService modeDateService = new ModeDateServiceLocator();
		ModeDateServicePortType client= modeDateService.getModeDateServiceHttpPort();
		/**
		 @param modeId 表单ID
		 @param Id 数据ID
		 @param userId 用户ID
		 @param right （y/n） 是否受权限控制 
		 * */
		System.out.println(client.deleteModeDataById(127, 1, 1, "Y"));
		
	}
	
	/**
	 * 获取表单数据总数
	 */
	public void getAllModeDataCount() throws ServiceException, RemoteException {
		ModeDateService modeDateService = new ModeDateServiceLocator();
		ModeDateServicePortType client= modeDateService.getModeDateServiceHttpPort();
		/*
		 * @param modeId 表单ID
		   @param userId 用户ID
		   @param conditions 查询条件
		   @param right （y/n） 是否受权限控制 
		 * */
		System.out.println(client.getAllModeDataCount(127, 1, "", "Y"));
	}
	
	/**
	 * 获取表单内容
	 */
	public void getModeDataByID() throws ServiceException, RemoteException {
		ModeDateService modeDateService = new ModeDateServiceLocator();
		ModeDateServicePortType client= modeDateService.getModeDateServiceHttpPort();
		/*
		 @param modeId 表单ID
		 @param Id 数据ID
		 @param userId 用户ID
		 @param right （y/n） 是否受权限控制
	     @param isReturnDetail （y/n） 是否返回明细表数据 
		 * */
		System.out.println(client.getModeDataByID(127, 3, 1,"Y", "Y"));
	}
	
	public void getAllModeDataList() throws ServiceException, RemoteException {
		ModeDateService modeDateService = new ModeDateServiceLocator();
		ModeDateServicePortType client= modeDateService.getModeDateServiceHttpPort();
		/*
		 * 
		@param modeId 表单ID
		@param pageNo 当前页数
		@param pageSize 每页记录数
		@param recordCount 记录总数（小于等于0时自动计算记录总数）
		@param userid 当前用户
		@param conditions 查询条件
		@param right （y/n） 是否受权限控制
		@param isReturnDetail （y/n） 是否返回明细表数据 
		 */
		System.out.println(client.getAllModeDataList(127, 1, 2,-1,1,"Y", "Y","N"));
	}
	
	

}
