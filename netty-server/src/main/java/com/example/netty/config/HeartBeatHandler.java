package com.example.netty.config;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @file: HeartBeatHandler
 * @author: Rainron
 * @date: 2021/4/29
 * description:
 */
@Slf4j
public class HeartBeatHandler extends SimpleChannelInboundHandler<String> {


    int readIdleTimes;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        log.info("HeartBeatHandler server message received : " + s);
        if("hello".equals(s)){
            ctx.channel().writeAndFlush("copy that");
        }else {
            System.out.println(" 其他信息处理 ... ");
        }
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent)evt;
        String eventType = null;
        switch (event.state()){
            case READER_IDLE:
                eventType = "读空闲";
                readIdleTimes ++; // 读空闲的计数加1
                break;
            case WRITER_IDLE:
                eventType = "写空闲";
                // 不处理
                break;
            case ALL_IDLE:
                eventType ="读写空闲";
                // 不处理
                break;
            default:
        }
        System.out.println(ctx.channel().remoteAddress() + "超时事件：" +eventType);
        if(readIdleTimes > 3){
            log.error("[server]读空闲超过3次，关闭连接");
            ctx.channel().writeAndFlush("you are out");
            ctx.channel().close();
        }
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("=== " + ctx.channel().remoteAddress() + " is active ===");
    }

}
