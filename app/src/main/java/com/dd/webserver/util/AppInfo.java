package com.dd.webserver.util;

import android.graphics.drawable.Drawable;

/**
 * Created by dong on 2015-12-30 0030.
 */
public class AppInfo {
    public String appName="";
    public String packageName="";
    public Drawable appIcon=null;
    public boolean isSystem = false;
    public boolean isRun = false;

    @Override
    public String toString() {
        return "AppName:"+appName+", packageName:"+packageName+", isSystem:"+isSystem+", isRun:"+isRun;
    }
}
