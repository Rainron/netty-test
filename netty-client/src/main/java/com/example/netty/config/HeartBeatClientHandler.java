package com.example.netty.config;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @file: HeartBeatClientHandler
 * @author: Rainron
 * @date: 2021/4/29
 * description:
 */
@Slf4j
public class HeartBeatClientHandler extends SimpleChannelInboundHandler<String>   {

    /**
     * PING消息
     */
    private static final String PING = "0";

    /**
     * PONG消息
     */
    private static final String PONG = "1";

    /**
     * 分隔符
     */
    private static final String SPLIT = "$_";

    //6s内收不到服务端的回应，则断开连接
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.error("IdleState die"+ctx.channel().remoteAddress());
                ctx.channel().close().sync();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("client channelRead..");
        log.info("receive "+ctx.channel().remoteAddress() + "server :" + msg);
    }
}
