package com.hanlin.fadp.base.component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.parsers.ParserConfigurationException;

import com.hanlin.fadp.base.util.UtilGenerics;
import com.hanlin.fadp.base.util.UtilURL;
import com.hanlin.fadp.base.util.UtilXml;
import com.hanlin.fadp.base.util.string.FlexibleStringExpander;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @ClassName: ComponentLoaderConfig 
 * @Description: 各个模块的component-load.xml解析类，该文件是关于各个组件模块的定义，
 * 该文件管理平台的顶级应用和一级应用，顶级应用配置文件在base/config下，各个一级应用的管理在
 * 一级应用的根目录下，顶级应用中使用的是load-components元素，一级应用使用的是load-component元素
 * @author: 祖国
 * @date: 2015年9月22日 上午9:42:22
 */
public class ComponentLoaderConfig {

    public static final String module = ComponentLoaderConfig.class.getName();
    /**模块配置文件常量名，即模块的component-load.xml文件  */
    public static final String COMPONENT_LOAD_XML_FILENAME = "component-load.xml";
    /**component-load.xml中节点元素为组件标记,即标记为load-component，
     * 出现在各个一级应用下面  */
    public static final int SINGLE_COMPONENT = 0;
    /**component-load.xml中节点为目录标记，即标记为load-components,只出现
     * 在base/config下面的component-load.xml文件中  */
    public static final int COMPONENT_DIRECTORY = 1;
    /**加载组件定义列表，并发环境中为自动更新  */
    private static final AtomicReference<List<ComponentDef>> componentDefsRef = new AtomicReference<List<ComponentDef>>(null);

    /**
     * @Title: getRootComponents 
     * @Description: 获取解析component-load.xml的解析数据，得到其load-component节点的list。
     * @param configFile 待解析的component-load.xml文件
     * @throws ComponentException
     * @return: List<ComponentDef>,为节点定义类的列表
     */
    public static List<ComponentDef> getRootComponents(String configFile) throws ComponentException {
        List<ComponentDef> existingInstance = componentDefsRef.get();
        if (existingInstance == null) {
            if (configFile == null) {
                configFile = COMPONENT_LOAD_XML_FILENAME;
            }
            URL xmlUrl = UtilURL.fromResource(configFile);
            List<ComponentDef> newInstance = getComponentsFromConfig(xmlUrl);
            if (componentDefsRef.compareAndSet(existingInstance, newInstance)) {
                existingInstance = newInstance;
            } else {
                existingInstance = componentDefsRef.get();
            }
        }
        return existingInstance;
    }

    /**
     * @Title: getComponentsFromConfig 
     * @Description: 解析一级应用下面的component-load.xml文件，返回 一个load-component节点的list,
     * load-component节点的解析类为ComponentDef类
     * @param configUrl component-load.xml文件对应的url
     * @throws ComponentException
     * @return: List<ComponentDef>
     */
    public static List<ComponentDef> getComponentsFromConfig(URL configUrl) throws ComponentException {
        if (configUrl == null) {
            throw new IllegalArgumentException("configUrl cannot be null");
        }
        Document document = null;
        try {
            document = UtilXml.readXmlDocument(configUrl, true);
        } catch (SAXException e) {
            throw new ComponentException("Error reading the component config file: " + configUrl, e);
        } catch (ParserConfigurationException e) {
            throw new ComponentException("Error reading the component config file: " + configUrl, e);
        } catch (IOException e) {
            throw new ComponentException("Error reading the component config file: " + configUrl, e);
        }
        Element root = document.getDocumentElement();
        List<? extends Element> toLoad = UtilXml.childElementList(root);
        List<ComponentDef> componentsFromConfig = null;
        if (!toLoad.isEmpty()) {
            componentsFromConfig = new ArrayList<ComponentDef>(toLoad.size());
            Map<String, ? extends Object> systemProps = UtilGenerics.<String, Object> checkMap(System.getProperties());
            for (Element element : toLoad) {
                String nodeName = element.getNodeName();
                String name = null;
                String location = null;
                int type = SINGLE_COMPONENT;
                if ("load-component".equals(nodeName)) {
                    name = element.getAttribute("component-name");
                    location = FlexibleStringExpander.expandString(element.getAttribute("component-location"), systemProps);
                } else if ("load-components".equals(nodeName)) {
                    location = FlexibleStringExpander.expandString(element.getAttribute("parent-directory"), systemProps);
                    type = COMPONENT_DIRECTORY;
                } else {
                    throw new ComponentException("Invalid element '" + nodeName + "' found in component config file " + configUrl);
                }
                componentsFromConfig.add(new ComponentDef(name, location, type));
            }
        }
        return Collections.unmodifiableList(componentsFromConfig);
    }

    /**
     * @ClassName: ComponentDef 
     * @Description: component-load.xml中文件定义对应的类，用此类装解析的一级应用的组件配置数据
     * @author: 祖国
     * @date: 2015年9月22日 上午10:16:36
     */
    public static class ComponentDef {
    	/**load-component节点的组件名component-name  */
        public String name;
        /**load-component节点component-location属性
         * 或load-components节点parent-directory  */
        public final String location;
        /**为load-component或load-components节点的标识  */
        public final int type;

        private ComponentDef(String name, String location, int type) {
            this.name = name;
            this.location = location;
            this.type = type;
        }
    }
    
    public static void main(String[] args){
    	try {
    		String configFile = "component-load.xml";
    		URL xmlUrl = UtilURL.fromResource(configFile);
    		System.out.println(xmlUrl);
    		//测试初始化加载，即加载component-load.xml
			List<ComponentLoaderConfig.ComponentDef> components = 
					ComponentLoaderConfig.getRootComponents(null);
			for(ComponentLoaderConfig.ComponentDef cc:components){
				System.out.println(cc.location+"..."+cc.name+"..."+cc.type);
			}
		} catch (ComponentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Map<String,String> map=System.getenv();
    	Iterator<Map.Entry<String, String>> it=map.entrySet().iterator();
    	while(it.hasNext()){
    		Map.Entry<String, String> entry=it.next();
    		System.out.println("key:"+entry.getKey()+"; value:"+entry.getValue());
    	}
    			
    }
}
