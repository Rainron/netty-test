package com.example.netty.config;

import com.example.netty.message.MessageInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @file: HeartBeatServerHandler
 * @author: Rainron
 * @date: 2021/4/30
 * description:
 */
@Slf4j
public class HeartBeatServerHandler extends SimpleChannelInboundHandler<MessageInfo>  {

    /**
     * PING消息
     */
    private static final String PING = "ping";

    /**
     * PONG消息
     */
    private static final String PONG = "pong";

    /**
     * 分隔符
     */
    private static final String SPLIT = "$_";

    //当服务器5秒内没有发生读的事件时，会触发这个事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) { //当事件为读事件触发时发生异常，或者中断
                //ctx.channel().close().sync();
                //log.error("已与 "+ctx.channel().remoteAddress()+" 断开连接");
                log.error("server IdleState read null ");
                //throw new Exception("idle exception");//将通道进行关闭
            }
        }
    }
    //当通道有读事件时
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.info("server channelRead..");
//        log.info(ctx.channel().remoteAddress() + "server :" + msg.toString());
//    }



    //当通道发生异常时
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server channel exception ,server close channel :"+cause.getMessage());
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageInfo msg) throws Exception {
        log.info("server channelRead..");
        log.info("receive "+ctx.channel().remoteAddress() + "client :" + msg.toString());
        if (PING.equals(msg.getBody())&& 0 == msg.getType()){
            log.info("receive msg"+msg.getBody());
        }else{
            ctx.fireChannelRead(msg);
        }


        log.info("reply "+ctx.channel().remoteAddress());
    }
}
