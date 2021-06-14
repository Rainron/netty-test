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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private static ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(SERVERNUM);
    private static String[] serverIps;
    private static int PORT;
    private Client[] clients = new Client[CLIENTNUM];
    private static final String PING = "0";


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
        serverIps = prop.ReadIPConfig(configPath,"serverIp");
        SERVERNUM = serverIps.length;

        //文件中的key值
        PORT = Integer.parseInt(prop.readValueForKey("port"));
        log.info("port:{}",PORT);
        log.info("serverIps:{}",serverIps.length);

        for(int i=0;i<serverIps.length;i++){
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

            bootstrap.remoteAddress(serverIps[i], PORT);
            bootstrap.handler(new MyClientInitializer());
            ChannelFuture future = null;
            try {
                future = bootstrap.connect(serverIps[i],PORT).sync();
            } catch (InterruptedException e) {
                log.error("InterruptedException:{}",getExceptionInfo(e));
            }
            if (future.isSuccess()) {
                socketChannels[i] = (SocketChannel)future.channel();
                log.info("Connect "+serverIps[i]+":"+PORT+" Server Success");
                //启动心跳机制
                //sendPing(socketChannels[i]);
            }else {
                log.info("Connect "+serverIps[i]+":"+PORT+" Server Error");
            }

        }
    }

    public NettyClient() throws Exception {

    }

    public NettyClient(int port, String host) throws Exception {

    }

    public  SocketChannel getSocketChannel() {
        int i = Select(SERVERNUM);
        return socketChannels[i];
    }


    //    private void start() throws Exception {
//        for (){
//            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
//            Bootstrap bootstrap = new Bootstrap();
//            //绑定
//            bootstrap.group(eventLoopGroup);
//            //指定NIO的模式，如果是客户端就是NioSocketChannel
//            bootstrap.channel(NioSocketChannel.class);
//
//            //TCP的缓冲区设置
//            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
//            //设置发送缓冲的大小
//            bootstrap.option(ChannelOption.SO_SNDBUF, 32 * 1024);
//            //设置接收缓冲区大小
//            bootstrap.option(ChannelOption.SO_RCVBUF, 32 * 1024);
//            // 有数据立即发送
//            bootstrap.option(ChannelOption.TCP_NODELAY, true);
//            // 保持连接
//            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
//
//            bootstrap.remoteAddress(this.host, this.port);
//            bootstrap.handler(new MyClientInitializer());
//
//            ChannelFuture future = bootstrap.connect(this.host, this.port).sync();
//            if (future.isSuccess()) {
//                socketChannel = (SocketChannel) future.channel();
//                log.error("connect server success");
//            }
//        }
//    }

    private static void sendPing(SocketChannel socketChannel) {
        while (true){
            MessageInfo req = new MessageInfo();
            req.setType(0);
            req.setBody("ping");
            socketChannel.writeAndFlush(req);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private  int Select(int total) {
        double r = 1;
        while (r == 1)
            r = Math.random();// 产生0到1之间的随机数
        return (int) (total * r);
    }

    public static String getExceptionInfo(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos));
        return baos.toString();
    }

}
