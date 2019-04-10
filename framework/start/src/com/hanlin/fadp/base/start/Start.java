package com.hanlin.fadp.base.start;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @ClassName: Start 
 * @Description: 启动类，一旦启动，将成为一个服务器
 * @author: turtle
 * @date: 2015年9月14日 下午1:21:22
 */
public final class Start {

    /*
     * This class implements a thread-safe state machine. The design is critical
     * for reliable starting and stopping of the server.
     * 
     * The machine's current state and state changes must be encapsulated in this
     * class. Client code may query the current state, but it may not change it.
     * 
     * This class uses a singleton pattern to guarantee that only one server instance
     * is running in the VM. Client code retrieves the instance by using the getInstance()
     * static method.
     * 
     */

	/**单例模式中的Start实例  */
    private static final Start instance = new Start();
    // DO NOT CHANGE THIS!
    private Start() {
    }
    /**
     * Returns the <code>Start</code> instance.
     */
    public static Start getInstance() {
        return instance;
    }

    /**
     * @Title: checkCommand 
     * @Description: 五种状态的检查，如果为help或输入状态和想要的状态相同，则返回想要的状态，如果输入状态为null，想要状态
     * 非空，则返回想要的状态，如果都不满足，则输出错误信息
     * @param command 输入状态
     * @param wanted 想要达到的状态
     * @return: Command
     */
    private static Command checkCommand(Command command, Command wanted) {
        if (wanted == Command.HELP || wanted.equals(command)) {
            return wanted;
        } else if (command == null) {
            return wanted;
        } else {
            System.err.println("Duplicate command detected(was " + command + ", wanted " + wanted);
            return Command.HELP_ERROR;
        }
    }

    private static void help(PrintStream out) {
        // Currently some commands have no dash, see FADP
        out.println("");
        out.println("Usage: java -jar fadp.jar [command] [arguments]");
        out.println("both    -----> Runs simultaneously the POS (Point of Sales) application and FADP standard");
        out.println("-help, -? ----> This screen");
        out.println("load-data -----> Creates tables/load data, eg: load-data -readers=seed,demo,ext -timeout=7200 -delegator=default -group=com.hanlin.fadp. Or: load-data -file=/tmp/dataload.xml");
        out.println("pos     -----> Runs the POS (Point of Sales) application");
        out.println("start -------> Starts the server");
        out.println("-status ------> Gives the status of the server");
        out.println("-shutdown ----> Shutdowns the server");
        out.println("test --------> Runs the JUnit test script");
        out.println("[no config] --> Uses default config");
        out.println("[no command] -> Starts the server with default config");
    }
	public static void restart(final long wait){
		Timer timer=new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
//					main(new String[]{"-shutdown"});
					String bat="\""+System.getProperty("fadp.home")+"/\" run.bat";
					Runtime.getRuntime().exec("cmd.exe /C start /D "+bat);
//					System.exit(0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, wait);
		
	}

    public static void main(String[] args) throws StartupException {
        Command command = null;
        //本方法中的loaderArgs并没有起作用
        List<String> loaderArgs = new ArrayList<String>(args.length);
        for (String arg : args) {
            if (arg.equals("-help") || arg.equals("-?")) {//如果输入-help，或-?则状态为help
                command = checkCommand(command, Command.HELP);
            } else if (arg.equals("-status")) {//如果输入-status，则状态为STATUS
                command = checkCommand(command, Command.STATUS);
            } else if (arg.equals("-shutdown")) {//如果输入-shutdown，则状态为SHUTDOWN
                command = checkCommand(command, Command.SHUTDOWN);
            } else if (arg.startsWith("-")) {//如果输入以-开头，且不含portoffset，则状态为COMMNAD，并去出参数
                if (!arg.contains("portoffset")) {
                    command = checkCommand(command, Command.COMMAND);
                }
                loaderArgs.add(arg.substring(1));//
            } else {//其它状态都是COMMON状态
                command = checkCommand(command, Command.COMMAND);
                if (command == Command.COMMAND) {
                    loaderArgs.add(arg);
                } else {
                    command = Command.HELP_ERROR;
                }
            }
        }
        if (command == null) {
            command = Command.COMMAND;
            loaderArgs.add("start");
        }
        if (command == Command.HELP) {
            help(System.out);
            return;
        } else if (command == Command.HELP_ERROR) {
            help(System.err);
            System.exit(1);
        }
        instance.init(args, command == Command.COMMAND);
        try {
            if (command == Command.STATUS) {
                System.out.println("Current Status : " + instance.status());
            } else if (command == Command.SHUTDOWN) {
                System.out.println("Shutting down server : " + instance.shutdown());
            } else {
                // general start
                instance.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(99);
        }
    }

    // ---------------------------------------------- //

    /** 启动时配置文件的配置信息类Config实例 */
    private Config config = null;//配置文件中的数据
    /** 启动时命令行参数 */
    private final List<String> loaderArgs = new ArrayList<String>();
    /**StartupLoader接口的实现类列表,只有两个实现类：ContainerLoader.java和SplashLoader.java，start中配置前者  */
    private final ArrayList<StartupLoader> loaders = new ArrayList<StartupLoader>();
    /**服务器状态，为AtomicReference，初始状态为 ServerState.STARTING */
    private final AtomicReference<ServerState> serverState = new AtomicReference<ServerState>(ServerState.STARTING);
    /**服务启动线程，初始化为用户线程  */
    private Thread adminPortThread = null;


/**
 * @Title: createListenerThread 
 * @Description: 创建线程监听器，启动服务器线程
 * @throws StartupException
 * @return: void
 */
    private void createListenerThread() throws StartupException {
        if (config.adminPort > 0) {
            this.adminPortThread = new AdminPortThread();
            this.adminPortThread.start();
        } else {
            System.out.println("Admin socket not configured; set to port 0");
        }
    }

    /**
     * @Title: createLogDirectory 
     * @Description: 创建日志文件目录
     * @return: void
     */
    private void createLogDirectory() {
        File logDir = new File(config.logDir);
        if (!logDir.exists()) {
            if (logDir.mkdir()) {
                System.out.println("Created FADP log dir [" + logDir.getAbsolutePath() + "]");
            }
        }
    }

    /**
     * Returns the server's current state.
     */
    public ServerState getCurrentState() {
        return serverState.get();
    }

    /**
     * @Title: init 
     * @Description: 平台初始化方法，包括全局配置Config初始化、创建日志目录、创建监听线程服务、设置关闭钩子、
     * 进入容器加载
     * @param args
     * @param fullInit
     * @throws StartupException
     * @return: void
     */
    void init(String[] args, boolean fullInit) throws StartupException {
    	//如果设置了系统属性文件，则加载该属性文件，平台没有
        String globalSystemPropsFileName = System.getProperty("fadp.system.props");
        if (globalSystemPropsFileName != null) {//启动时候是没有设置fadp.system.props的
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(globalSystemPropsFileName);
                System.getProperties().load(stream);
            } catch (IOException e) {
                throw new StartupException("Couldn't load global system props", e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        throw new StartupException("Couldn't close stream", e);
                    }
                }
            }
        }
        //加载配置文件，需要根据传入的参数进行加载，先读取start代码包下面的properties文件来设置系统全局变量
        try {
            this.config = new Config(args);
        } catch (IOException e) {
            throw new StartupException("Couldn't not fetch config instance", e);
        }
        // parse the startup arguments
        if (args.length > 1) {
            this.loaderArgs.addAll(Arrays.asList(args).subList(1, args.length));
            // Needed when portoffset is used with these commands
            try {
                if ("status".equals(args[0])) {//读取服务器状态
                    System.out.println("Current Status : " + instance.status());
                } else if ("stop".equals(args[0])) {
                    System.out.println("Shutting down server : " + instance.shutdown());
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(99);
            }
        }
        if (!fullInit) {
            return;
        }
        //创建日志目录
        createLogDirectory();
        //构建监听线程，确保当前线程处于运行状态 ，一旦系统启动，将自动成为一个服务器
        createListenerThread();
        // 设置关闭钩子set the shutdown hook
        if (config.useShutdownHook) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    shutdownServer();
                }
            });
        } else {
            System.out.println("Shutdown hook disabled");
        }

        // initialize the startup loaders
        initStartLoaders();
    }

    /**
     * @Title: createClassLoader 
     * @Description: 构建一个线程上下文类加载器<code>NativeLibClassLoader</code>实例，将涉及的相关库加入到该实例的lib路径中
     * @throws IOException
     * @return: NativeLibClassLoader
     */
    private NativeLibClassLoader createClassLoader() throws IOException {
    	//获取当前线程的类加载器
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        //如果是自定义的线程上下文类加载器，则获取其父类加载器
        if (parent instanceof NativeLibClassLoader) {
            parent = parent.getParent();
        }
        /**
         * 如果自定义的线程上下文类加载器为null，则先获取Start类对象的类加载器，如还为null，
         * 则获取JVM的类加载器
         */
        if (parent == null) {
            parent = Start.class.getClassLoader();
            if (parent == null) {
                parent = ClassLoader.getSystemClassLoader();
            }
        }
        
        Classpath classPath = new Classpath();
        /* 将StartupLoaders正常工作所需资源加入到classpath资源收集器中
         */
       //将应用路径加入到classpath的组件列表中
        classPath.addComponent(config.fadpHome);
        String fadpHomeTmp = config.fadpHome;
        if (!fadpHomeTmp.isEmpty() && !fadpHomeTmp.endsWith("/")) {
        	fadpHomeTmp = fadpHomeTmp.concat("/");
        }
        //framework/base/config,framework/base/dtd路径添加到classpath的组件列表中
        if (config.classpathAddComponent != null) {
            String[] components = config.classpathAddComponent.split(",");
            for (String component : components) {
                classPath.addComponent(fadpHomeTmp.concat(component.trim()));
            }
        }
        /**
         * 将framework/base/lib,framework/base/lib/commons,framework/base/build/lib 
         * 加入到classpath的组件列表中
         */
        if (config.classpathAddFilesFromPath != null) {
            String[] paths = config.classpathAddFilesFromPath.split(",");
            for (String path : paths) {
                classPath.addFilesFromPath(new File(fadpHomeTmp.concat(path.trim())));
            }
        }
        
        //根据前面的URL和加载器构建一个新的加载器
        NativeLibClassLoader classloader = new NativeLibClassLoader(classPath.getUrls(), parent);
        //如果是测试，则加载测试的相关类
        if (config.instrumenterFile != null && config.instrumenterClassName != null) {
            try {
                classloader = new InstrumentingClassLoader(classPath.getUrls(), parent, config.instrumenterFile,
                        config.instrumenterClassName);
            } catch (Exception e) {
                System.out.println("Instrumenter not enabled - " + e);
            }
        }
        //将jdk的路径放到当前线程的上下文类加载器的lib路径中，即NativeLibClassLoader的libPaths中
        classloader.addNativeClassPath(System.getProperty("java.library.path"));
        //将classpath实例中的native库路径加入到当前线程的上下文类加载器的lib路径中
        for (File folder : classPath.getNativeFolders()) {
            classloader.addNativeClassPath(folder);
        }
        return classloader;
    }

    /**
     * @Title: initStartLoaders 
     * @Description:初始化当前线程上下文类加载器,进入容器加载
     * @throws StartupException
     * @return: void
     */
    private void initStartLoaders() throws StartupException {
    	//构建当前线程上下文类加载器，并设置到当前线程中
        NativeLibClassLoader classloader = null;
        try {
            classloader = createClassLoader();
        } catch (IOException e) {
            throw new StartupException("Couldn't create NativeLibClassLoader", e);
        }
        Thread.currentThread().setContextClassLoader(classloader);
        
        /**
         * 进入容器加载
         */
        String[] argsArray = loaderArgs.toArray(new String[loaderArgs.size()]);
        synchronized (this.loaders) {
            for (Map<String, String> loaderMap : config.loaders) {
                if (this.serverState.get() == ServerState.STOPPING) {
                    return;
                }
                try {
                    String loaderClassName = loaderMap.get("class");
                    Class<?> loaderClass = classloader.loadClass(loaderClassName);
                    StartupLoader loader = (StartupLoader) loaderClass.newInstance();
                    loaders.add(loader); // add before loading, so unload can occur if error during loading
                    loader.load(config, argsArray);//调用ContainerLoader.java中的load方法
                } catch (ClassNotFoundException e) {
                    throw new StartupException(e.getMessage(), e);
                } catch (InstantiationException e) {
                    throw new StartupException(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    throw new StartupException(e.getMessage(), e);
                }
            }
            this.loaders.trimToSize();
        }
        if (classloader instanceof InstrumentingClassLoader) {
            try {
                ((InstrumentingClassLoader)classloader).closeInstrumenter();
            } catch (IOException e) {
                throw new StartupException(e.getMessage(), e);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String path : classloader.getNativeLibPaths()) {
            if (sb.length() > 0) {
                sb.append(File.pathSeparator);
            }
            sb.append(path);
        }
        System.setProperty("java.library.path", sb.toString());
    }

    /**
     * @Title: sendSocketCommand 
     * @Description: 发送一个socket命令，该命令使用control对象，读取返回的状态值
     * @param control
     * @throws IOException
     * @throws ConnectException
     * @return: String
     */
    private String sendSocketCommand(Control control) throws IOException, ConnectException {
        String response = "FADP is Down";
        try {
            Socket socket = new Socket(config.adminAddress, config.adminPort);
            // send the command
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(config.adminKey + ":" + control);
            writer.flush();
            // read the reply
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            response = reader.readLine();
            reader.close();
            // close the socket
            writer.close();
            socket.close();

        } catch (ConnectException e) {
            System.out.println("Could not connect to " + config.adminAddress + ":" + config.adminPort);
        }
        return response;
    }

    private String shutdown() throws IOException {
        return sendSocketCommand(Control.SHUTDOWN);
    }

    /**
     * @Title: shutdownServer 
     * @Description: TODO
     * @return: void
     */
    void shutdownServer() {
        ServerState currentState;
        do {//本循环使用了线程同步锁
            currentState = this.serverState.get();
            if (currentState == ServerState.STOPPING) {
                return;
            }
        } while (!this.serverState.compareAndSet(currentState, ServerState.STOPPING));//如果当前值 == 预期值，则以原子方式将该值设置为给定的更新值。
        // The current thread was the one that successfully changed the state;
        // continue with further processing.
        synchronized (this.loaders) {//同步卸载启动的组件，仅对接口编程
            // Unload in reverse order
            for (int i = this.loaders.size(); i > 0; i--) {
                StartupLoader loader = this.loaders.get(i - 1);
                try {
                    loader.unload();
                } catch (Exception e) {
                    e.printStackTrace();                }
            }
        }
        //设置线程的中断状态，让用户自己选择时间地点去结束线程，这一句很重要，否则会产生异常
        if (this.adminPortThread != null && this.adminPortThread.isAlive()) {
            this.adminPortThread.interrupt();
        }
    }

    /**
     * Returns <code>true</code> if all loaders were started.
     * 
     * @return <code>true</code> if all loaders were started.
     */
    boolean startStartLoaders() {
        synchronized (this.loaders) {
            // start the loaders
            for (StartupLoader loader : this.loaders) {
                if (this.serverState.get() == ServerState.STOPPING) {
                    return false;
                }
                try {
                    loader.start();
                } catch (StartupException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return this.serverState.compareAndSet(ServerState.STARTING, ServerState.RUNNING);
    }

    /**
     * @Title: status 
     * @Description: 调用socket，发送一个状态请求
     * @throws IOException
     * @return: String
     */
    private String status() throws IOException {
        try {
            return sendSocketCommand(Control.STATUS);
        } catch (ConnectException e) {
            return "Not Running";
        } catch (IOException e) {
            throw e;
        }
    }

    void stopServer() {
        shutdownServer();
        System.exit(0);
    }

    /**
     * @Title: start 
     * @Description: TODO
     * @throws Exception
     * @return: void
     */
    void start() throws Exception {
        if (!startStartLoaders()) {
            if (this.serverState.get() == ServerState.STOPPING) {
                return;
            } else {
                throw new Exception("Error during start.");
            }
        }
        if (config.shutdownAfterLoad) {
            stopServer();
        }
    }

    public Config getConfig() {
        return this.config;
    }

    // ----------------------------------------------- //

    /**
     * @ClassName: AdminPortThread 
     * @Description: 应用使用的监督线程，启动时设置为用户线程，并且在start调用前启动
     * @author: 祖国
     * @date: 2015年9月26日 上午6:24:24
     */
    private class AdminPortThread extends Thread {
        private ServerSocket serverSocket = null;

        AdminPortThread() throws StartupException {
            super("FADP-AdminPortThread");
            try {
                this.serverSocket = new ServerSocket(config.adminPort, 1, config.adminAddress);
            } catch (IOException e) {
                throw new StartupException("Couldn't create server socket(" + config.adminAddress + ":" + config.adminPort + ")",
                        e);
            }
            setDaemon(false);//设置为用户线程，在线程启动前调用
        }

        private void processClientRequest(Socket client) throws IOException {
            BufferedReader reader = null;
            PrintWriter writer = null;
            try {
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String request = reader.readLine();
                writer = new PrintWriter(client.getOutputStream(), true);
                Control control;
                if (request != null && !request.isEmpty() && request.contains(":")) {
                    String key = request.substring(0, request.indexOf(':'));
                    if (key.equals(config.adminKey)) {
                        control = Control.valueOf(request.substring(request.indexOf(':') + 1));
                        if (control == null) {
                            control = Control.FAIL;
                        }
                    } else {
                        control = Control.FAIL;
                    }
                } else {
                    control = Control.FAIL;
                }
                control.processRequest(Start.this, writer);//向流中写入服务器状态
            } finally {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
        }

        @Override
        public void run() {
            System.out.println("Admin socket configured on - " + config.adminAddress + ":" + config.adminPort);
            while (!Thread.interrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Received connection from - " + clientSocket.getInetAddress() + " : "
                            + clientSocket.getPort());
                    processClientRequest(clientSocket);
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @ClassName: Command 
     * @Description: 定义了五种命令枚举：HELP, HELP_ERROR, STATUS, SHUTDOWN, COMMAND，
     * 用于main方法中作为启动参数
     * @author: turtle
     * @date: 2015年9月14日 下午2:26:52
     */
    private enum Command {
        HELP, HELP_ERROR, STATUS, SHUTDOWN, COMMAND
    }

    /**
     * @ClassName: Control 
     * @Description: 输出服务器状态
     * @author: 祖国
     * @date: 2015年9月26日 上午7:00:58
     */
    private enum Control {
        SHUTDOWN {
            @Override
            void processRequest(Start start, PrintWriter writer) {
                if (start.serverState.get() == ServerState.STOPPING) {
                    writer.println("IN-PROGRESS");
                } else {
                    writer.println("OK");
                    writer.flush();
                    start.stopServer();
                }
            }
        },
        STATUS {
            @Override
            void processRequest(Start start, PrintWriter writer) {
                writer.println(start.serverState.get());
            }
        },
        FAIL {
            @Override
            void processRequest(Start start, PrintWriter writer) {
                writer.println("FAIL");
            }
        };

        abstract void processRequest(Start start, PrintWriter writer);
    }

    /**
     * @ClassName: ServerState 
     * @Description: 定义服务器的三种状态
     * @author: turtle
     * @date: 2015年9月15日 上午7:30:24
     */
    public enum ServerState {
        STARTING, RUNNING, STOPPING;

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }
    
    //test
    public static void main11(String[] args) throws StartupException {
 /*   	//===下面为测试命令行的代码
    	Command command = null;
        List<String> loaderArgs = new ArrayList<String>(args.length);
        for (String arg : args) {
            if (arg.equals("-help") || arg.equals("-?")) {//如果输入-help，或-?则状态为help
                command = checkCommand(command, Command.HELP);
                System.out.println("hhhh"+command);
            } else if (arg.equals("-status")) {//如果输入-status，则状态为STATUS
                command = checkCommand(command, Command.STATUS);
                loaderArgs.add("hhhh");
            } else if (arg.equals("-shutdown")) {//如果输入-shutdown，则状态为SHUTDOWN
                command = checkCommand(command, Command.SHUTDOWN);
            } else if (arg.startsWith("-")) {//如果输入以-开头，且不含portoffset，则状态为COMMNAD，并去出参数
                if (!arg.contains("portoffset")) {
                    command = checkCommand(command, Command.COMMAND);
                }
                loaderArgs.add(arg.substring(1));//
            } else {//其它状态都是COMMON状态
                command = checkCommand(command, Command.COMMAND);
                if (command == Command.COMMAND) {
                    loaderArgs.add(arg);
                    System.out.println(arg);
                } else {
                    command = Command.HELP_ERROR;
                }
            }
        }*/
        
    	//===下面为测试用户线程的代码
    	Start start=new Start();
    	String[] str={"start"};
    	start.init(str, true);
/*		AdminPortThread adminPortThread=start.new AdminPortThread();
		adminPortThread.start();*/
    	try {
			start.createClassLoader();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}
