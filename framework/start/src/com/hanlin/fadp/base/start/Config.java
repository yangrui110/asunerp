
package com.hanlin.fadp.base.start;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @ClassName: Config 
 * @Description: 启动时，配置文件的配置信息类，根据启动时候的输入参数，决定加载com/hanlin/fadp/base/start/xxx.properties文件，
 * 有七种输入情况，输入的参数和相关properties文件名一致
 * @author: turtle
 * @date: 2015年9月14日 下午1:18:42
 */
public class Config {

	/**ip地址，默认是本机，部署时候需要配置start.properties中的fadp.admin.host项  */
    public final InetAddress adminAddress;//
    /**admin的key，默认值见start.properties中配置  */
    public final String adminKey;//
    /** admin端口号，默认是10523 */
    public final int adminPort;//
    /**是否设置awtHeadless模式  */
    public final String awtHeadless;
    /**值为framework/base/config/fadp-containers.xml  */
    public final String containerConfig;//
    /**测试类名，值为com.hanlin.fadp.base.config.CoberturaInstrumenter  */
    public final String instrumenterClassName;//
    /** 测试数据文件，值为runtime/logs/cobertura-base.dat */
    public final String instrumenterFile;//
    /** 容器加载器相关信息，只有一个map，其中有两个key-value，key=class，value=com.hanlin.fadp.base.container.ContainerLoader，key=profile，value=main,rmi */
    public final List<Map<String, String>> loaders;//
    /** 日志路径，runtime/logs */
    public final String logDir;//
    /** 当前应用路径 */
    public final String fadpHome;//
    /**自动关闭钩子，默认false，但在load-data.properties文件中设置为true，即加启动时输入loa-data参数，则载数据后会自动关闭  */
    public final boolean shutdownAfterLoad;//
    /**splash风格的logo，用于pos情况  */
    public final String splashLogo;
    /** 关闭钩子，设置为true */
    public final boolean useShutdownHook;//
    /**偏移端口  */
    public final Integer portOffset;
    /** 值为framework/base/config,framework/base/dtd */
    public final String classpathAddComponent;//
    /** 值为framework/base/lib,framework/base/lib/commons,framework/base/build/lib */
    public final String classpathAddFilesFromPath;//

    /**
     * @Title:Config
     * @Description:读取com/hanlin/fadp/base/start/xxx.properties文件进行初始化
     * @param args 输入的参数，一般是启动系统时候从控制台输入的参数
     * @throws IOException
     */
    Config(String[] args) throws IOException {
        String firstArg = args.length > 0 ? args[0] : "";
        // Needed when portoffset is used with these commands, start.properties fits for all of them
        if ("start-batch".equalsIgnoreCase(firstArg)
                || "start-debug".equalsIgnoreCase(firstArg)
                || "stop".equalsIgnoreCase(firstArg)
                || "-shutdown".equalsIgnoreCase(firstArg) // shutdown & status hack (was pre-existing to portoffset introduction, also useful with it)
                || "-status".equalsIgnoreCase(firstArg)) {
            firstArg = "start";
        }
        // default command is "start"
        if (firstArg == null || firstArg.trim().length() == 0) {
            firstArg = "start";
        }
        /**
         * 传入的参数可以是both、load-data、pos、rmi、start、
         * test、testlist等七个，相应对应的文件位于start代码包下面
         */
        String config =  "com/hanlin/fadp/base/start/" + firstArg + ".properties";

        //通过上一步获得的string，加载配置文件
        Properties props = this.getPropertiesFile(config);
        System.out.println("Start.java using configuration file " + config);

        // 从配置的属性文件中获取 fadp.home值，设置为平台的home，默认为"."，表示当前用户路径，作为平台的根路径，并设置到环境变量中
        String fadpHomeTmp = props.getProperty("fadp.home", ".");
        // get a full path
        if (fadpHomeTmp.equals(".")) {
            fadpHomeTmp = System.getProperty("user.dir");
            fadpHomeTmp = fadpHomeTmp.replace('\\', '/');
        }
        fadpHome = fadpHomeTmp;
        System.setProperty("fadp.home", fadpHome);
        System.out.println("Set FADP_HOME to - " + fadpHome);
        
        //通过上一步获取的properties流，从中获取组件配置文件config路径
        classpathAddComponent = props.getProperty("fadp.start.classpath.addComponent");
        
        //进一步从properties流中获取组件加载器、组件、容器等平台相关的配置文件路径，基本jar文件路径
        classpathAddFilesFromPath = props.getProperty("fadp.start.classpath.addFilesFromPath");
        // 进一步从properties流中获取日志路径，runtime/logs为默认值
        logDir = getFadpHomeProp(props, "fadp.log.dir", "runtime/logs");

        // container configuration
        containerConfig = getFadpHomeProp(props, "fadp.container.config", "framework/base/config/fadp-containers.xml");

        // get the admin server info
        String serverHost = getProp(props, "fadp.admin.host", "127.0.0.1");

        String adminPortStr = getProp(props, "fadp.admin.port", "0");
        // set the admin key
        adminKey = getProp(props, "fadp.admin.key", "NA");

        // 创建主机地址（InetAddress）
        adminAddress = InetAddress.getByName(serverHost);

        // 解析端口号，还没有使用过
        int adminPortTmp;
        try {
            adminPortTmp = Integer.parseInt(adminPortStr);
            if (args.length > 0) {
                for (String arg : args) {
                    if (arg.toLowerCase().contains("portoffset=") && !arg.toLowerCase().contains("${portoffset}")) {
                        adminPortTmp = adminPortTmp != 0 ? adminPortTmp : 10523; // This is necessary because the ASF machines don't allow ports 1 to 3, see  INFRA-6790
                        adminPortTmp += Integer.parseInt(arg.split("=")[1]);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error while parsing admin port number (so default to 10523) = " + e);
            adminPortTmp = 10523;
        }
        adminPort = adminPortTmp;

        // 设置derby的环境变量
        String derbyPath = getProp(props, "derby.system.home", "runtime/data/derby");
        System.setProperty("derby.system.home", derbyPath);

        // 关闭钩子check for shutdown hook
        if (System.getProperty("fadp.enable.hook") != null && System.getProperty("fadp.enable.hook").length() > 0) {
            useShutdownHook = "true".equalsIgnoreCase(System.getProperty("fadp.enable.hook"));
        } else if (props.getProperty("fadp.enable.hook") != null && props.getProperty("fadp.enable.hook").length() > 0) {
            useShutdownHook = "true".equalsIgnoreCase(props.getProperty("fadp.enable.hook"));
        } else {
            useShutdownHook = true;
        }

        // 自动关闭钩子check for auto-shutdown
        if (System.getProperty("fadp.auto.shutdown") != null && System.getProperty("fadp.auto.shutdown").length() > 0) {
            shutdownAfterLoad = "true".equalsIgnoreCase(System.getProperty("fadp.auto.shutdown"));
        } else if (props.getProperty("fadp.auto.shutdown") != null && props.getProperty("fadp.auto.shutdown").length() > 0) {
            shutdownAfterLoad = "true".equalsIgnoreCase(props.getProperty("fadp.auto.shutdown"));
        } else {
            shutdownAfterLoad = false;
        }

        // set AWT headless mode
        awtHeadless = getProp(props, "java.awt.headless", null);
        if (awtHeadless != null) {
            System.setProperty("java.awt.headless", awtHeadless);
        }

        // get the splash logo
        splashLogo = props.getProperty("fadp.start.splash.logo", null);

        // 设置默认的国际化set the default locale
        String localeString = props.getProperty("fadp.locale.default");
        if (localeString != null && localeString.length() > 0) {
            String locales[] = localeString.split("_");
            switch (locales.length) {
                case 1:
                    Locale.setDefault(new Locale(locales[0]));
                    break;
                case 2:
                    Locale.setDefault(new Locale(locales[0], locales[1]));
                    break;
                case 3:
                    Locale.setDefault(new Locale(locales[0], locales[1], locales[2]));
            }
            System.setProperty("user.language", localeString);
        }

        // 设置时区set the default time zone
        String tzString = props.getProperty("fadp.timeZone.default");
        if (tzString != null && tzString.length() > 0) {
            TimeZone.setDefault(TimeZone.getTimeZone(tzString));
        }

        //测试类名
        instrumenterClassName = getProp(props, "fadp.instrumenterClassName", null);
        //测试文件
        instrumenterFile = getProp(props, "fadp.instrumenterFile", null);

        /** 
         * 类加载器相关loaders，有两个key-value，
         * key=class，value=com.hanlin.fadp.base.container.ContainerLoader，
         * key=profile，value=main,rmi
         */
        ArrayList<Map<String, String>> loadersTmp = new ArrayList<Map<String, String>>();
        int currentPosition = 1;
        Map<String, String> loader = null;
        while (true) {
            loader = new HashMap<String, String>();
            String loaderClass = props.getProperty("fadp.start.loader" + currentPosition);
            if (loaderClass == null || loaderClass.length() == 0) {
                break;
            } else {
                loader.put("class", loaderClass);
                loader.put("profiles", props.getProperty("fadp.start.loader" + currentPosition + ".loaders"));
                loadersTmp.add(Collections.unmodifiableMap(loader));
                currentPosition++;
            }
        }
        loadersTmp.trimToSize();
        loaders = Collections.unmodifiableList(loadersTmp);

        // set the port offset
        Integer portOffset = 0;
        if (args != null) {
            for (String argument : args) {
                // arguments can prefix w/ a '-'. Just strip them off
                if (argument.startsWith("-")) {
                    int subIdx = 1;
                    if (argument.startsWith("--")) {
                        subIdx = 2;
                    }
                    argument = argument.substring(subIdx);
                }
                // parse the arguments
                if (argument.indexOf("=") != -1) {
                    String argumentName = argument.substring(0, argument.indexOf("="));
                    String argumentVal = argument.substring(argument.indexOf("=") + 1);
                    if ("portoffset".equalsIgnoreCase(argumentName) && !"${portoffset}".equals(argumentVal)) {
                        try {
                            portOffset = Integer.valueOf(argumentVal);
                        } catch (NumberFormatException e) {
                            System.out.println("Error while parsing portoffset (the default value 0 will be used) = " + e);
                        }
                    }
                }
            }
        }
        this.portOffset = portOffset;

    }

    /**
     * @Title: getfadpHomeProp 
     * @Description: 设置包括当前应用路径下的路径
     * @param props
     * @param key
     * @param def
     * @return: String 完整路径
     */
    private String getFadpHomeProp(Properties props, String key, String def) {
        String value = System.getProperty(key);
        if (value != null)
            return value;
        return fadpHome + "/" + props.getProperty(key, def);
    }

    /**
     * @Title: getProp 
     * @Description: 从属性文件中获取key对应的value，不存在，则返回默认值
     * @param props
     * @param key
     * @param def
     * @return: String
     */
    private String getProp(Properties props, String key, String def) {
        String value = System.getProperty(key);
        if (value != null)
            return value;
        return props.getProperty(key, def);
    }

    /**
     * @Title: getPropertiesFile 
     * @Description: 加载当前类实例所在包下面的配置文件
     * @param config
     * @throws IOException
     * @return: Properties
     */
    private Properties getPropertiesFile(String config) throws IOException {
        InputStream propsStream = null;
        Properties props = new Properties();
        try {
            // first try classpath
            propsStream = getClass().getClassLoader().getResourceAsStream(config);
            if (propsStream != null) {
                props.load(propsStream);
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            // next try file location
            File propsFile = new File(config);
            if (propsFile != null) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(propsFile);
                    if (fis != null) {
                        props.load(fis);
                    }
                } catch (FileNotFoundException e2) {
                    // do nothing; we will see empty props below
                } finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
            }
        } finally {
            if (propsStream != null) {
                propsStream.close();
            }
        }

        // check for empty properties
        if (props.isEmpty()) {
            throw new IOException("Cannot load configuration properties : " + config);
        }
        return props;
    }
}
