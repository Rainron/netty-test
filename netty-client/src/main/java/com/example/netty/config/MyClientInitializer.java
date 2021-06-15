package com.example.netty.config;

import com.example.netty.message.MessageDecoder;
import com.example.netty.message.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @file: MyClientInitializer
 * @author: Rainron
 * @date: 2021/4/28
 * description:
 */
public class MyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        //ChannelPipeline类是ChannelHandler实例对象的链表，用于处理或截获通道的接收和发送数据
        ChannelPipeline pipeline = sc.pipeline();
        // 也可以选择将处理器加到pipeLine的那个位置
        //一些限定和编码解码器
//        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
//        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new MessageEncoder());
        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new NettyClientHandler());
        //心跳机制 客户端写服务端返回的东西
        pipeline.addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS));
        pipeline.addLast(new HeartBeatClientHandler());
    }
}
