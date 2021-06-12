package com.example.netty.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Properties;

/**
 * @file: PropertiesUtils
 * @author: Rainron
 * @date: 2021/4/29
 * description:
 */
@Slf4j
public class PropertiesUtils {

    private static Properties props;

    public PropertiesUtils(String filePath) {
        getProperties(filePath);
    }

    public void getProperties(String filePath) {
        if (StringUtils.isEmpty(filePath.trim())){
            log.error("filePath:",filePath);
        }

        try {
            props = new Properties();
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            props.load(in);
        } catch (Exception e) {
            log.error("Exception:{}",e.getMessage());
        }
    }

    /**
     * 根据key读取value
     * @param key
     * @return
     */
    public  String readValueForKey(String key) {
        InputStream in = null;
        try {
            return props.getProperty(key);
        } catch (Exception e) {
            log.error("Exception:{}",e.getMessage());
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("IOException:{}",e.getMessage());
            }
        }
    }

    /**
     * 根据key读取value
     * @param filePath
     * @param key
     * @return
     */
    public  String readValue(String filePath,String key) {
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(filePath));
            if (in != null) {
                prop.load(in);
            }
            return prop.getProperty(key);
        } catch (Exception e) {
            log.error("Exception:{}",e.getMessage());
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("IOException:{}",e.getMessage());
            }
        }
    }

    public String[] ReadIPConfig(String filePath,String Item) {
        log.info("enter ReadIPConfig:" + Item);
        log.info("the path of user home is :" + filePath);
        try {
            FileInputStream conf = new FileInputStream(filePath);
            //FileInputStream conf = new FileInputStream("test.ini");
            byte[] readData = new byte[1000];
            int num = conf.read(readData);
            conf.close();
            // 寻找配置项
            String confData = new String(readData);
            int index = confData.indexOf(Item);
            index += Item.length();
            String content = confData.substring(index + 1, confData.indexOf("p", index));
            String[] newipcontent = content.trim().split(",");
            int countnum = 0;
            for (int k = 0; k < newipcontent.length; k++) {
                if (newipcontent[k] == null || newipcontent[k].length() == 0)
                    countnum++;
            }
            String[] ipcontent = new String[newipcontent.length - countnum];
            int j = 0;
            for (int i = 0; i < newipcontent.length; i++) {
                if (newipcontent[i] != null && newipcontent[i].length() != 0)
                    ipcontent[j++] = newipcontent[i];
            }

            return ipcontent;
        } catch (Exception e) {
            log.error("ReadConfig:" + Item + "exception msg:" + e);
            return null;
            //throw new GeneralException("ReadIPConfig:" + Item + " Exception:" + e.getMessage());
        }

    }
}
