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
        log.info("客户端 Active .....");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext clx, MessageInfo msg) throws Exception {
        log.info("客户端收到消息: {}", msg.toString());
        System.out.println("服务端:"+clx.channel().remoteAddress()+"-----"+msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught: {}",cause.toString());
        log.error("server ip: {}",ctx.channel().localAddress().toString());
        ctx.close();
    }


}
