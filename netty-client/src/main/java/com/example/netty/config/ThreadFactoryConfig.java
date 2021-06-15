package com.example.netty.config;


import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @file: ThreadFactoryConfig
 * @author: Rainron
 * @date: 2021/6/9
 * description:
 */
public class ThreadFactoryConfig implements ThreadFactory {

    /**
     *原子操作保证每个线程都有唯一的
     */
    private static final AtomicInteger threadNumber=new AtomicInteger(1);

    private final AtomicInteger mThreadNum = new AtomicInteger(1);

    private final String prefix;

    private final boolean daemoThread;

    private final ThreadGroup threadGroup;

    public ThreadFactoryConfig() {
        this("socket-pool-" + threadNumber.getAndIncrement(), false);
    }

    public ThreadFactoryConfig(String prefix) {
        this(prefix, false);
    }


    public ThreadFactoryConfig(String prefix, boolean setDaemon) {
        this.prefix = StringUtils.isNotEmpty(prefix) ? prefix + "-" + threadNumber.getAndIncrement() + "-thread-" : "";
        daemoThread = setDaemon;
        SecurityManager s = System.getSecurityManager();
        threadGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = prefix + mThreadNum.getAndIncrement();
        Thread t = new Thread(threadGroup, runnable, name, 0);
        t.setDaemon(daemoThread);
        return t;
    }

}
