package com.example.netty.config;

import com.example.netty.message.MessageInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @file: NettyServerHandler
 * @author: Rainron
 * @date: 2021/4/28
 * description:
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageInfo> {

    private static final int BUS = 0;
    private static final int HEART = 1;
    private static final String PING = "ping";
    private static final String PONG = "pong";

    /**
     * 客户端连接会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Sever Channel active:"+" ip: {}",ctx.channel().remoteAddress());
    }

    /**
     * 客户端发消息会触发
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageInfo msg) throws Exception {
        MessageInfo req = new MessageInfo();
        if (msg.getType()==BUS){
            log.info("服务器收到: {} 客户端发来的消息: {} ", ctx.channel().remoteAddress(),msg.toString());
            req.setType(BUS);
            req.setCmd(0x1002);
            req.setBody("我收到了="+String.valueOf((new Date()).getTime()));
            req.setBodyLength(String.valueOf((new Date()).getTime()).length());
            ctx.writeAndFlush(req);
        }else if (msg.getType()==HEART){
            log.info("服务器收到: {} 客户端发来的心跳消息", ctx.channel().remoteAddress());
            req.setType(HEART);
            req.setBody(PONG);
            log.info("服务器回应客户端心跳");
            ctx.writeAndFlush(req);
        }else {
            log.error("服务器收到: {} 客户端发来的错误类型:{} ", ctx.channel().remoteAddress(),msg.getType());
        }

    }

    /**
     * 发生异常触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ExceptionCaught: {}",cause.toString());
        log.error("Client ip: {}",ctx.channel().localAddress().toString());
        ctx.close();
    }
}
