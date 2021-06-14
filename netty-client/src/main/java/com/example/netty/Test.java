package com.example.netty;

import com.alibaba.fastjson.JSON;
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
        NettyClient bootstrap = new NettyClient();
        int i = 1;
        System.out.println(LocalDateTime.now());
        TimeUnit.SECONDS.sleep(2);
        MessageInfo req = new MessageInfo();
        req.setType(0);
        req.setCmd(0x1001);
        req.setBody(String.valueOf((new Date()).getTime()));
        req.setBodyLength(String.valueOf((new Date()).getTime()).length());
        bootstrap.getSocketChannel().writeAndFlush(req);

//        while (true) {
//            System.out.println(LocalDateTime.now());
//            TimeUnit.SECONDS.sleep(2);
//            MessageInfo req = new MessageInfo();
//            req.setType(0);
//            req.setCmd(0x1001);
//            req.setBody(String.valueOf((new Date()).getTime()));
//            req.setBodyLength(String.valueOf((new Date()).getTime()).length());
//            bootstrap.getSocketChannel().writeAndFlush(req);
//        }
    }
}
