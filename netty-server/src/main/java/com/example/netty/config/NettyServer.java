package com.example.netty.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

/**
 * @file: NettyServer
 * @author: Rainron
 * @date: 2021/4/28
 * description:
 */
@Slf4j
public class NettyServer {

    static {
        String filePath = NettyServer.class.getClassLoader().getResource("logback-spring.xml").getPath();
        log.info("filePath:{}",filePath);
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
    }

    private Integer port;
    private SocketChannel socketChannel;
    public NettyServer(Integer port) throws Exception {
        this.port = port;
        bind(port);
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }
    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
    private void bind(int serverPort) throws Exception {
        // ????????????group ???????????????Client????????????
        EventLoopGroup boss = new NioEventLoopGroup();
        // ????????????group ?????????????????????????????????
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            // ????????????group
            bootstrap.group(boss, worker);
            //??????NIO????????????????????????????????????NioSocketChannel
            bootstrap.channel(NioServerSocketChannel.class);
            //TCP??????????????????
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            //???????????????????????????
            bootstrap.childOption(ChannelOption.SO_SNDBUF, 32 * 1024);
            //???????????????????????????
            bootstrap.childOption(ChannelOption.SO_RCVBUF, 32 * 1024);
            // ?????????????????????
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            // ????????????
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            //??????????????????????????????
            bootstrap.childHandler(new MyServerInitializer());

            ChannelFuture future = bootstrap.bind(serverPort).sync();
            log.info("Server ip: {}",future.channel().remoteAddress());
            log.info("Server Accept: {}", port);
            if (future.isSuccess()) {
                log.info("Long Connection Started Success");
            } else {
                log.error("Long Connection Started Failure");
            }
            //????????????(??????????????????????????????????????????)
            future.channel().closeFuture().sync();
        }catch (InterruptedException e){
            e.printStackTrace();
            log.error("InterruptedException:{}",getExceptionInfo(e));
        }finally {
            boss.shutdownGracefully();//????????????
            worker.shutdownGracefully();//????????????
        }


    }
    public static String getExceptionInfo(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos));
        return baos.toString();
    }

}
