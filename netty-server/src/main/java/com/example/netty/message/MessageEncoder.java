package com.example.netty.message;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @file: MessageEncoder
 * @author: Rainron
 * @date: 2021/4/28
 * description:
 */
@Slf4j
public class MessageEncoder extends MessageToByteEncoder<MessageInfo> {

    private static final String DEFAULT_ENCODE = "utf-8";
    private static final int MAGIC_NUMBER = 0x01;

    public MessageEncoder() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageInfo msg, ByteBuf out) throws Exception {
        String jsonStr = JSON.toJSONString(msg);
        byte[] body = jsonStr.getBytes();
        int dataLength = body.length;  //读取消息的长度
        out.writeInt(dataLength);  //先将消息长度写入，也就是消息头
        out.writeBytes(body);  //消息体中包含我们要发送的数据
    }
}
