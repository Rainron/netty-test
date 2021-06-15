package com.example.netty.config;

import com.example.netty.message.MessageInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SocketChannel;
import java.util.Date;

/**
 * @file: NettyClientHandler
 * @author: Rainron
 * @date: 2021/4/28
 * description:
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageInfo> {

    private static final int BUS = 0;
    private static final int HEART = 1;
    private static final String PING = "ping";
    private static final String PONG = "pong";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client Channel active:"+" ip: {}",ctx.channel().remoteAddress());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageInfo msg) throws Exception {
        MessageInfo req = new MessageInfo();
        if (msg.getType()==BUS){
            log.info("客户端收到: {} 服务器返回发来的消息: {} ", ctx.channel().remoteAddress(),msg.toString());
        }else if (msg.getType()==HEART && PONG.equals(msg.getBody())){
            log.info("客户端收到: {} 服务器返回的心跳消息", ctx.channel().remoteAddress());
        }else {
            log.error("客户端收到: {} 服务器发来的错误类型:{} ", ctx.channel().remoteAddress(),msg.getType());
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught: {}",cause.toString());
        log.error("server ip: {}",ctx.channel().localAddress().toString());
        ctx.close();
    }


}
