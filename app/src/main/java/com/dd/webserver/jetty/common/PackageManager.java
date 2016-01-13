package com.dd.webserver.jetty.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import com.dd.webserver.util.AppInfo;
import com.dd.webserver.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dong on 2015-12-30 0030.
 */
public class PackageManager {
    private static PackageManager packageManager = null;
    private static String APP_DIR = "/app";
    private ArrayList<AppInfo> appList;
    private List<PackageInfo> packages;
    private Context mContext;

    private PackageManager(Context mContext) {
        this.mContext = mContext;
        appList = new ArrayList<AppInfo>();
        packages = mContext.getPackageManager().getInstalledPackages(0);
    }

    public static PackageManager getInstance(Context context) {
        if (packageManager == null) packageManager = new PackageManager(context);
        return packageManager;
    }

    public synchronized void collectAppInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<packages.size();i++) {
                    PackageInfo packageInfo = packages.get(i);
                    AppInfo tmpInfo =new AppInfo();
                    tmpInfo.appName = packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
                    tmpInfo.packageName = packageInfo.packageName;
                    tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(mContext.getPackageManager());
                    tmpInfo.isSystem = (packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0 ? false : true;
                    appList.add(tmpInfo);
                }
                if(appList.size() <= 0 || Utils.configWorkPath == null) return;
                verifyAppDir();
                for (int i = 0; i < appList.size(); i++) {
                    saveBitmap(drawableToBitmap(appList.get(i).appIcon), Utils.configWorkPath+APP_DIR+"/"+appList.get(i).packageName+".png", 100);
                }
            }
        }).start();
    }

    public ArrayList<AppInfo> getAppList() {
        return appList;
    }

    private void verifyAppDir() {
        File dir = new File(Utils.configWorkPath+APP_DIR);
        if(!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Drawable转化为Bitmap
     */
    private  Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将bitmap保存到本地
     *
     * @param bitmap  图片资源
     * @param path    需要保存的路径
     * @param quality 保存的图片质量
     */
    private void saveBitmap(Bitmap bitmap, String path, int quality) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
        }
    }
}
