package com.example.netty.config;

import com.example.netty.message.MessageDecoder;
import com.example.netty.message.MessageEncoder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @file: MyServerInitializer
 * @author: Rainron
 * @date: 2021/4/28
 * description:
 */
public class MyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        //ChannelPipeline类是ChannelHandler实例对象的链表，用于处理或截获通道的接收和发送数据
        ChannelPipeline pipeline = sc.pipeline();
        //解决TCP粘包拆包的问题，以特定的字符结尾（$_）
        //pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Unpooled.copiedBuffer("$_".getBytes())));
        //一些限定和编码解码器
        pipeline.addLast("MessageDecoder",new MessageDecoder());
        pipeline.addLast("MessageEncoder",new MessageEncoder());
        pipeline.addLast(new NettyServerHandler());
        ////心跳机制 服务端读客户端发送的东西
        pipeline.addLast(new IdleStateHandler(5,0,0,TimeUnit.SECONDS));
        pipeline.addLast(new HeartBeatServerHandler());
    }
}
