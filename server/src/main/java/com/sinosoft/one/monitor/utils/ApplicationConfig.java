package com.sinosoft.one.monitor.utils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: daojian
 * Date: 13-12-25
 * Time: 下午2:09
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationConfig {
    private final static Properties props = new Properties();
    private static final String monitorIp="monitor.ip";
    private static final String monitorPort="monitor.port";

    static {
        try {
            Reader reader = new InputStreamReader(ApplicationConfig.class.getClassLoader().getResourceAsStream("application.properties"));
            props.load(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getMonitorIp(){
        return props.getProperty(monitorIp);
    }
    public static String getMonitorPort(){
        return props.getProperty(monitorPort);
    }
}
