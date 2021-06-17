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

    /*
    * @param: [ctx, evt]
    * @Return: void
    * @Date: 2021/6/17
    * @Description: 10s内收不到服务端的回应，则断开连接
    **/
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                ctx.channel().close().sync();
                log.error("Client IdleState read null ");
                log.error("已与:{}{}",ctx.channel().remoteAddress(),"断开连接");

                log.error("正在与:{}尝试重连",ctx.channel().remoteAddress());
                //将通道进行关闭
                throw new Exception("idle exception");
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("client channelRead..");
        log.info("receive "+ctx.channel().remoteAddress() + "server :" + msg);
    }
}
