package com.example.netty.config;

import com.example.netty.message.MessageInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @file: NettyClientHandler
 * @author: Rainron
 * @date: 2021/4/28
 * description:
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageInfo> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client Channel active:"+" ip: {}",ctx.channel().remoteAddress());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageInfo msg) throws Exception {
        log.info("客户端收到: {} 服务端发来的的消息: {} ", ctx.channel().remoteAddress(),msg.toString());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught: {}",cause.toString());
        log.error("server ip: {}",ctx.channel().localAddress().toString());
        ctx.close();
    }


}
