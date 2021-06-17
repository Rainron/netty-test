package com.example.netty.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.example.netty.message.MessageInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.*;

/**
 * @file: NettyClient
 * @author: Rainron
 * @date: 2021/4/28
 * description:
 */
@Slf4j
public class NettyClient {

    private static int CLIENTNUM = 1;
    private static int SERVERNUM = 1;
    private static SocketChannel[] socketChannels = new SocketChannel[SERVERNUM];
    private static ChannelFuture[] futures = new ChannelFuture[SERVERNUM];
    private static String[] serverIps;
    private static int PORT;
    private Client[] clients = new Client[CLIENTNUM];
    private static final ThreadPoolExecutor heartPool;
    private static final int HEART = 1;
    private static final String PING = "ping";
    private static final String PONG = "pong";


    //先读取自定义logback配置文件路径 再读取连接方式
    //再读取服务端配置文件
    static {
        String filePath = Objects.requireNonNull(NettyClient.class.getClassLoader().getResource("logback-spring.xml")).getPath();
        File logbackFile = new File(filePath);
        if (logbackFile.exists()) {
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            lc.reset();
            try {
                configurator.doConfigure(logbackFile);
            }
            catch (JoranException e) {
                log.error("logback configurator logbackFile error msg:{}",e.getMessage());
                System.exit(-1);
            }
        }else {
            throw new RuntimeException("logback-spring.xml path no file exist:"+filePath);
        }
        log.info("开始读取配置文件");
        String configPath = Objects.requireNonNull(NettyClient.class.getClassLoader().getResource("config.properties")).getPath();
        PropertiesUtils prop = new PropertiesUtils(configPath);
        String serverIp = prop.readValueForKey("serverIps");
        serverIps = serverIp.split(",");
        SERVERNUM = serverIps.length;
        //文件中的key值
        PORT = Integer.parseInt(prop.readValueForKey("port"));
        log.info("port:{}",PORT);
        log.info("serverIpCount:{}",serverIps.length);

        for(int i=0;i<serverIps.length;i++){
            doConnect(serverIps[i],PORT,i);
        }
        //内部线程池
        heartPool =  new ThreadPoolExecutor(5, 10,
                0L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),new ThreadFactoryConfig("interior"));

        new Timer().schedule(new heartBeatTask(), 1000, 5000);
        new Timer().schedule(new reConnectTask(), 5000, 8000);
    }

    public static void doConnect(String host,int port,int i){
        try {
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            //绑定
            bootstrap.group(eventLoopGroup);
            //指定NIO的模式，如果是客户端就是NioSocketChannel
            bootstrap.channel(NioSocketChannel.class);
            //TCP的缓冲区设置
            //bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            //设置发送缓冲的大小
            bootstrap.option(ChannelOption.SO_SNDBUF, 32 * 1024);
            //设置接收缓冲区大小
            bootstrap.option(ChannelOption.SO_RCVBUF, 32 * 1024);
            // 有数据立即发送
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 保持连接
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            bootstrap.remoteAddress(host, port);
            bootstrap.handler(new MyClientInitializer());
            futures[i] = bootstrap.connect(host,port).sync();
            if (futures[i].isSuccess()) {
                socketChannels[i] = (SocketChannel)futures[i].channel();
                log.info("Connect "+host+":"+port+" Server Success");
            }else {
                log.info("Connect "+host+":"+port+" Server Error");
            }
        }catch (InterruptedException e){
            log.error("Client Connect Server Error serverIp:{} port:{} InterruptedException:{}",host,port,getExceptionInfo(e));
        }catch (Exception e){
            log.error("Client Connect Server Error serverIp:{} port:{} Exception:{}",host,port,getExceptionInfo(e));

        }
    }

    static class heartBeatTask extends java.util.TimerTask {

        @Override
        public void run() {
            heartPool.execute(new MyTaskThread1());
        }
    }

    static class MyTaskThread1 implements Runnable {

        @Override
        public void run() {
            for(int i=0;i<serverIps.length;i++){
                log.info("正在向：{}-服务器 {}",serverIps[i],"发送心跳");
                sendPing(socketChannels[i]);
            }
        }
    }

    static class reConnectTask extends java.util.TimerTask {

        @Override
        public void run() {
            heartPool.execute(new MyTaskThread2());
        }
    }

    static class MyTaskThread2 implements Runnable {

        @Override
        public void run() {
            for(int i=0;i<serverIps.length;i++){
                if (!socketChannels[i].isActive()){
                    log.error("开始对：{} {}",serverIps[i],"重连");
                    doConnect(serverIps[i],PORT,i);
                    if (socketChannels[i].isActive()){
                        log.error("{} {}",serverIps[i],"重连成功");
                    }

                }
            }
        }
    }

    public NettyClient()  {

    }

    public NettyClient(int port, String host)  {

    }

    public  SocketChannel getSocketChannel() {
        int i = Select(SERVERNUM);
        return socketChannels[i];
    }

    private static void sendPing(SocketChannel socketChannel) {

        MessageInfo req = new MessageInfo();
        req.setType(HEART);
        req.setBody(PING);
        socketChannel.writeAndFlush(req);

    }

    private  int Select(int total) {
        double r = 1;
        while (r == 1) {
            // 产生0到1之间的随机数
            r = Math.random();
        }
        return (int) (total * r);
    }

    public static String getExceptionInfo(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos));
        return baos.toString();
    }

}
