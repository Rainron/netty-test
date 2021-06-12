package com.example.netty;

import com.example.netty.config.NettyServer;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Test {
    public static void main(String[] args) throws Exception {
        //启动服务端
        try{
            log.info("开启启动服务端");
            new NettyServer(8008);
        }catch (Exception e){
            log.error("NettyServer Exception:{}",e.getMessage());
        }
    }

}
