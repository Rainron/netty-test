package com.example.netty.utils;

import java.util.ResourceBundle;

/**
 * @file: PropertiesUtil
 * @author: Rainron
 * @date: 2021/4/29
 * description:
 */
public class PropertiesUtil {
    private static ResourceBundle resourceBundle;

    static {
        //properties文件的名称
        //resourceBundle=ResourceBundle.getBundle("config");
    }

    public static String getVal(String key){
        return resourceBundle.getString(key);//文件中的key值
    }
}
