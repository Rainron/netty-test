package com.example.netty.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @file: MessageDecoder
 * @author: Rainron
 * @date: 2021/4/28
 * description:
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {

    private static final int MAGIC_NUMBER = 0x01;

    private static final int HEAD_LENGTH = 4;

    public MessageDecoder() {

    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < HEAD_LENGTH) {  //这个HEAD_LENGTH是我们用于表示头长度的字节数。
            log.error("decode readableBytes error: {}",in.readableBytes());
            return;
        }
        in.markReaderIndex();                  //我们标记一下当前的readIndex的位置
        int dataLength = in.readInt();       // 读取传送过来的消息的长度。ByteBuf 的readInt()方法会让他的readIndex增加4
        if (dataLength < 0) { // 我们读到的消息体长度为0，这是不应该出现的情况，这里出现这情况，关闭连接。
            log.error("decode dataLength error: {}",dataLength);
            ctx.close();
        }

        if (in.readableBytes() != dataLength) { //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
            log.error("decode readableBytes error readableBytes: {}",in.readableBytes()," dataLength:{}",dataLength);
            in.resetReaderIndex();
            return;
        }

        byte[] body = new byte[dataLength];  //把传送过来的数据，取出来吧~~
        in.readBytes(body);  //
        //Object o = JSON.parseObject(String.valueOf(body));  //将byte数据转化为我们需要的对象
        //将byte数据转化为我们需要的对象。伪代码，用什么序列化，自行选择
        Object  o =  convertToObj(body);
        MessageInfo msg = JSON.parseObject((String) o, new TypeReference<MessageInfo>(){});
        //log.error("msg: {}",msg);
        int type = msg.getType();
        out.add(msg);


    }
    private Object convertToObj(byte[] body) {
        return new String(body,0,body.length);
    }

    public static String Bytes2HexString(byte[] b) {
        if (b == null) {
            return null;
        } else {
            String hexs = "";
            for (int i = 0; i < b.length; i++) {
                String hex = Integer.toHexString(b[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                hexs = hexs + hex.toUpperCase();
            }
            return hexs;
        }

    }
}
