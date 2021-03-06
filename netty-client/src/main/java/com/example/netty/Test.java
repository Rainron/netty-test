package com.example.netty;

import com.example.netty.config.NettyClient;
import com.example.netty.message.MessageInfo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @file: Test
 * @author: Rainron
 * @date: 2021/4/28
 * description:
 */
public class Test {

    public static void main(String[] args) throws Exception {

        //启动netty客户端
        NettyClient nettyClient = new NettyClient();
        TimeUnit.SECONDS.sleep(2);
        MessageInfo req = new MessageInfo();
        req.setType(0);
        req.setCmd(0x1001);
        req.setBody(String.valueOf((new Date()).getTime()));
        req.setBodyLength(String.valueOf((new Date()).getTime()).length());
        nettyClient.getSocketChannel().writeAndFlush(req);


    }

}
