package com.hanlin.fadp.base.container;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import com.hanlin.fadp.base.lang.LockedBy;
import com.hanlin.fadp.base.util.StringUtil;
import com.hanlin.fadp.base.util.UtilURL;
import com.hanlin.fadp.base.util.UtilValidate;
import com.hanlin.fadp.base.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @ClassName: ContainerConfig 
 * @Description: 解析xml文件中container节点的配置，如framework/config/fadp-containers.xml文件，
 * 该文件是关于组件容器的配置数据，又如fadp-component.xml中container的配置数据
 * @author: turtle
 * @date: 2015年9月16日 下午4:00:52
 */
public class ContainerConfig {

    public static final String module = ContainerConfig.class.getName();

    /**container节点信息解析map，fadp-containers.xml中关于container节点信息解析的map，
     * key=container节点的name属性，value=ContainerConfig.Container实例   */
    @LockedBy("ContainerConfig.class")
    private static Map<String, Container> containers = new LinkedHashMap<String, Container>();

    /**
     * @Title: getContainer 
     * @Description: 从容器的配置文件fadp-containers.xml中获取container节点的实例
     * @param containerName 待获取的container节点的name属性
     * @param configFile 配置文件
     * @throws ContainerException
     * @return: Container 一个ContainerConfig.Container实例
     */
    public static Container getContainer(String containerName, String configFile) throws ContainerException {
        Container container = containers.get(containerName);
        if (container == null) {
            getContainers(configFile);
            container = containers.get(containerName);
        }
        if (container == null) {
            throw new ContainerException("No container found with the name : " + containerName);
        }
        return container;
    }

    /**
     * @Title: getContainers 
     * @Description: 获取关于Container的配置数据，调用了同名方法，本方法主要解决传入参数为空的检查和将string转化为URL
     * @param configFile
     * @throws ContainerException
     * @return: Collection<Container>
     */
    public static Collection<Container> getContainers(String configFile) throws ContainerException {
        if (UtilValidate.isEmpty(configFile)) {
            throw new ContainerException("configFile argument cannot be null or empty");
        }
        URL xmlUrl = UtilURL.fromResource(configFile);
        if (xmlUrl == null) {
            throw new ContainerException("Could not find container config file " + configFile);
        }
        return getContainers(xmlUrl);
    }

    /**
     * @Title: getContainers 
     * @Description: 获取关于Container的配置数据，调用了getContainerPropsFromXml方法，该方法通过传入的url获取
     * 关于容器配置数据实例的Collection，遍历该集合
     * @param xmlUrl
     * @throws ContainerException
     * @return: Collection<Container>
     */
    public static Collection<Container> getContainers(URL xmlUrl) throws ContainerException {
        if (xmlUrl == null) {
            throw new ContainerException("xmlUrl argument cannot be null");
        }
        Collection<Container> result = getContainerPropsFromXml(xmlUrl);
        synchronized (ContainerConfig.class) {
            for (Container container : result) {
                containers.put(container.name, container);//要测试？
            }
        }
        return result;
    }

    public static String getPropertyValue(ContainerConfig.Container parentProp, String name, String defaultValue) {
        ContainerConfig.Container.Property prop = parentProp.getProperty(name);
        if (prop == null || UtilValidate.isEmpty(prop.value)) {
            return defaultValue;
        } else {
            return prop.value;
        }
    }

    public static int getPropertyValue(ContainerConfig.Container parentProp, String name, int defaultValue) {
        ContainerConfig.Container.Property prop = parentProp.getProperty(name);
        if (prop == null || UtilValidate.isEmpty(prop.value)) {
            return defaultValue;
        } else {
            int num = defaultValue;
            try {
                num = Integer.parseInt(prop.value);
            } catch (Exception e) {
                return defaultValue;
            }
            return num;
        }
    }

    public static boolean getPropertyValue(ContainerConfig.Container parentProp, String name, boolean defaultValue) {
        ContainerConfig.Container.Property prop = parentProp.getProperty(name);
        if (prop == null || UtilValidate.isEmpty(prop.value)) {
            return defaultValue;
        } else {
            return "true".equalsIgnoreCase(prop.value);
        }
    }

    public static String getPropertyValue(ContainerConfig.Container.Property parentProp, String name, String defaultValue) {
        ContainerConfig.Container.Property prop = parentProp.getProperty(name);
        if (prop == null || UtilValidate.isEmpty(prop.value)) {
            return defaultValue;
        } else {
            return prop.value;
        }
    }

    public static int getPropertyValue(ContainerConfig.Container.Property parentProp, String name, int defaultValue) {
        ContainerConfig.Container.Property prop = parentProp.getProperty(name);
        if (prop == null || UtilValidate.isEmpty(prop.value)) {
            return defaultValue;
        } else {
            int num = defaultValue;
            try {
                num = Integer.parseInt(prop.value);
            } catch (Exception e) {
                return defaultValue;
            }
            return num;
        }
    }

    public static boolean getPropertyValue(ContainerConfig.Container.Property parentProp, String name, boolean defaultValue) {
        ContainerConfig.Container.Property prop = parentProp.getProperty(name);
        if (prop == null || UtilValidate.isEmpty(prop.value)) {
            return defaultValue;
        } else {
            return "true".equalsIgnoreCase(prop.value);
        }
    }

    private ContainerConfig() {}

    /**
     * @Title: getContainerPropsFromXml 
     * @Description: 通过获取fadp-containers.xml和fadp-component.xml中的container元素构建ContainerConfig.Container对象的集合
     * @param xmlUrl
     * @throws ContainerException
     * @return: Collection<Container>
     */
    private static Collection<Container> getContainerPropsFromXml(URL xmlUrl) throws ContainerException {
        Document containerDocument = null;
        try {
            containerDocument = UtilXml.readXmlDocument(xmlUrl, true);
        } catch (SAXException e) {
            throw new ContainerException("Error reading the container config file: " + xmlUrl, e);
        } catch (ParserConfigurationException e) {
            throw new ContainerException("Error reading the container config file: " + xmlUrl, e);
        } catch (IOException e) {
            throw new ContainerException("Error reading the container config file: " + xmlUrl, e);
        }
        Element root = containerDocument.getDocumentElement();
        List<Container> result = new ArrayList<Container>();
        for (Element curElement: UtilXml.childElementList(root, "container")) {
            result.add(new Container(curElement));
        }
        return result;
    }

    /**
     * @ClassName: Container 
     * @Description: fadp-containers.xml中的container元素对应的解析类
     * @author: turtle
     * @date: 2015年9月17日 下午8:53:06
     */
    public static class Container {
    	/**fadp-containers.xml中container元素的name属性  */
        public final String name;
        /**fadp-containers.xml中container元素的class属性   */
        public final String className;
        /**fadp-containers.xml中container元素的loaders属性，为一个list */
        public final List<String> loaders;
        /**fadp-containers.xml中container元素的下的property子元素的map，
         * key=property的name属性，value=Property实例  */
        public final Map<String, Property> properties;

        public Container(Element element) {
            this.name = element.getAttribute("name");
            this.className = element.getAttribute("class");
            this.loaders = StringUtil.split(element.getAttribute("loaders"), ",");

            properties = new LinkedHashMap<String, Property>();
            for (Element curElement: UtilXml.childElementList(element, "property")) {
                Property property = new Property(curElement);
                properties.put(property.name, property);
            }
        }

        public Property getProperty(String name) {
            return properties.get(name);
        }

        /**
         * @Title: getPropertiesWithValue 
         * @Description: 根据给定的value值，遍历container元素的property子元素的map，找property子元素的value值和传入的值
         * 相等的所有property实例，返回一个list，
         * @param value
         * @return: List<Property>
         */
        public List<Property> getPropertiesWithValue(String value) {
            List<Property> props = new LinkedList<Property>();
            if (UtilValidate.isNotEmpty(properties)) {
                for (Property p: properties.values()) {
                    if (p != null && value.equals(p.value)) {
                        props.add(p);
                    }
                }
            }
            return props;
        }

        /**
         * @ClassName: Property 
         * @Description: fadp-containers.xml中的container元素的property子元素对应的解析类
         * @author: turtle
         * @date: 2015年9月17日 下午9:07:21
         */
        public static class Property {
        	/**fadp-containers.xml中的container元素的property子元素的name属性  */
            public String name;
            /**fadp-containers.xml中的container元素的property子元素value属性，
             * 如value属性为空，则取property-value属性 */
            public String value;
            /**fadp-containers.xml中的container元素的property子元素的property子元素集合  */
            public Map<String, Property> properties;

            public Property(Element element) {
            	
                this.name = element.getAttribute("name");
                this.value = element.getAttribute("value");
//if(this.name.equals("keyAlias")){
//	System.out.println(name+":------:"+value);
//}
                if (UtilValidate.isEmpty(this.value)) {
                    this.value = UtilXml.childElementValue(element, "property-value");
                }

                properties = new LinkedHashMap<String, Property>();
                for (Element curElement: UtilXml.childElementList(element, "property")) {
                    Property property = new Property(curElement);
                    properties.put(property.name, property);
                }
            }

            public Property getProperty(String name) {
                return properties.get(name);
            }

            public List<Property> getPropertiesWithValue(String value) {
                List<Property> props = new LinkedList<Property>();
                if (UtilValidate.isNotEmpty(properties)) {
                    for (Property p: properties.values()) {
                        if (p != null && value.equals(p.value)) {
                            props.add(p);
                        }
                    }
                }
                return props;
            }
        }
    }
}
