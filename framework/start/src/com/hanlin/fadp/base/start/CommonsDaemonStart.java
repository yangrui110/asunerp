

package com.hanlin.fadp.base.start;


/**
 * Special startup class for Commons Daemon users.
 * 
 * @see <a href="http://commons.apache.org/proper/commons-daemon/jsvc.html">Commons Daemon</a>
 */
/**
 * @ClassName: CommonsDaemonStart 
 * @Description: TODO
 * @author: turtle
 * @date: 2015年9月14日 下午1:17:37
 */
public final class CommonsDaemonStart {

    public CommonsDaemonStart() {
    }

    public void init(String[] args) throws StartupException {
        Start.getInstance().init(args, true);
    }

    public void destroy() {
        // FIXME: undo init() calls.
    }

    public void start() throws Exception {
        Start.getInstance().start();
    }

    public void stop() {
        Start.getInstance().shutdownServer();
    }
}
