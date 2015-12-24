package com.dd.webserver.util;

import android.content.Context;
import android.widget.Space;

/**
 * Created by Administrator on 2015-12-23 0023.
 */
public class Utils {
    public static String CONFIG_ASSETS_DIR = "jetty";
    public static String CONFIG_DOWNLOAD_DIR = "download";

    public static String configWorkPath = null;
    public static String configDownloadPath = null;

    public static String CONFIG_LS = System.getProperty("line.separator");
    public static Context JServerContext = null;

    public static String serverIpPort = null;
    public static String serverUpload = "/upload";
    public static String serverScreencap = "/screencap";

    public static boolean isConfigWorkPathVaild() {
        if (configWorkPath == null || configWorkPath.isEmpty()) return false;
        return true;
    }

    public static boolean isConfigDownloadPathVaild() {
        if(configDownloadPath == null || configDownloadPath.isEmpty()) return false;
        return true;
    }

    public static boolean isServerIpPortVaild() {
        if(serverIpPort == null) return false;
        return true;
    }

    public static StringBuilder getErrorInfo(String title, String message) {

        StringBuilder sb = new StringBuilder();
        sb.append("<html>").append(CONFIG_LS);
        sb.append("<head><title>").append("").append("</title></head>").append(CONFIG_LS);
        sb.append("<body><h1>").append(title).append("</h1>").append(CONFIG_LS);
        sb.append("<p>").append(message).append("</p>").append(CONFIG_LS);
        sb.append("</body></html>").append(CONFIG_LS);
        return sb;
    }
}
