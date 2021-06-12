package com.example.netty.message;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @file: Message
 * @author: Rainron
 * @date: 2021/4/28
 * description:
 */
@Data
@Slf4j
public class MessageInfo implements Serializable {

    private Integer type; //0 业务接口 1 心跳
    private Integer cmd; // 指令码
    private String body; //报文
    private Integer bodyLength; //报文长度

}
