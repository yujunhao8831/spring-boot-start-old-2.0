package com.aidijing.deploy;


/**
 * @author : 披荆斩棘
 * @date : 2016/12/1
 */
public abstract class AppConfig {

    /**
     * 测试环境 : DEBUG 改为 true
     * 生产环境 : DEBUG 改为 false
     */
    private static volatile boolean debug = false;

    public static boolean isDebug () {
        return debug;
    }

    public static void setDebug ( boolean debug ) {
        AppConfig.debug = debug;
    }


}










