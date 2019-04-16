package com.stereo.study.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;


/**
 * Created by liujing11 on 2015/5/11.
 */
public class Daemon extends Thread {

    private static Logger LOG = LoggerFactory.getLogger(Daemon.class);

    {
        setDaemon(true);
    }
    Runnable runnable = null;

    public Daemon() {
        super();
    }

    public Daemon(Runnable runnable) {
        super(runnable);
        this.runnable = runnable;
        this.setName(((Object) runnable).toString());
    }

    public Daemon(ThreadGroup group, Runnable runnable) {
        super(group, runnable);
        this.runnable = runnable;
        this.setName(((Object) runnable).toString());
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public static class DaemonFactory extends Daemon implements ThreadFactory {

        @Override
        public Thread newThread(Runnable runnable)
        {
            SecurityManager securityManager = System.getSecurityManager();
            ThreadGroup group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
            Daemon daemon = new Daemon(group,runnable);
            if (daemon.getPriority() != Thread.NORM_PRIORITY)
                daemon.setPriority(Thread.NORM_PRIORITY);
            return daemon;
        }
    }
}