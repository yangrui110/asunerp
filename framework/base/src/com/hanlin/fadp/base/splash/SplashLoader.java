
package com.hanlin.fadp.base.splash;

import java.awt.EventQueue;

import com.hanlin.fadp.base.start.Config;
import com.hanlin.fadp.base.start.StartupException;
import com.hanlin.fadp.base.start.StartupLoader;

public class SplashLoader implements StartupLoader, Runnable {

    public static final String module = SplashLoader.class.getName();
    private static SplashScreen screen = null;
    private Config config = null;

    /**
     * Load a startup class
     *
     * @param config Startup config
     * @param args   Input arguments
     * @throws com.hanlin.fadp.base.start.StartupException
     *
     */
    public void load(Config config, String args[]) throws StartupException {
        this.config = config;

        Thread t = new Thread(this);
        t.setName(this.toString());
        t.setDaemon(false);
        t.run();
    }

    /**
     * Start the startup class
     *
     * @throws com.hanlin.fadp.base.start.StartupException
     *
     */
    public void start() throws StartupException {
    }

    /**
     * Stop the container
     *
     * @throws com.hanlin.fadp.base.start.StartupException
     *
     */
    public void unload() throws StartupException {
        SplashLoader.close();
    }

    public static SplashScreen getSplashScreen() {
        return screen;
    }

    public static void close() {
        if (screen != null) {
            EventQueue.invokeLater(new SplashScreenCloser());
        }
    }

    public void run() {
        if (config.splashLogo != null) {
            screen = new SplashScreen(config.splashLogo);
            screen.splash();
        }
    }

    private static final class SplashScreenCloser implements Runnable {
        public void run() {
            screen.close();
            screen = null;
        }
    }
}
