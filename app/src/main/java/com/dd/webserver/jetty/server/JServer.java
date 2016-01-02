package com.dd.webserver.jetty.server;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.dd.webserver.jetty.servlet.AppManagerServlet;
import com.dd.webserver.jetty.servlet.ScreenCapServlet;
import com.dd.webserver.jetty.servlet.UploadApkServlet;
import com.dd.webserver.ui.PackageManager;
import com.dd.webserver.util.Utils;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dong on 2015-12-23 0023.
 */
public class JServer {
    private static final String TAG = "JServer";
    private Context mContext;
    private int mPort;
    private Server mServer;
    private File mWorkPathFile = null;
    private PackageManager packageManager;
    private AppManagerServlet managerServlet;

    public JServer(Context context, int port) {
        mContext = context;
        Utils.JServerContext = mContext;
        mPort = port;
        mWorkPathFile = getWorkPath();
        Utils.configWorkPath = mWorkPathFile.getAbsolutePath()+"/"+Utils.CONFIG_ASSETS_DIR;

        packageManager = PackageManager.getInstance(mContext);
        packageManager.collectAppInfo();
        managerServlet = new AppManagerServlet(mContext);
    }

    /**
     * start server
     */
    public synchronized void start() {
        if ((mServer != null) && (mServer.isStarted())) {
            return;
        }
        if (mServer == null) {
            // setup Servlet Handler
            ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            servletHandler.addServlet(new ServletHolder(new UploadApkServlet()), Utils.serverUpload);
            servletHandler.addServlet(new ServletHolder(new ScreenCapServlet()), Utils.serverScreencap);
            servletHandler.addServlet(new ServletHolder(managerServlet), Utils.serverAppList);
            servletHandler.addServlet(new ServletHolder(managerServlet), Utils.serverAppRun);
            servletHandler.addServlet(new ServletHolder(managerServlet), Utils.serverAppStop);
            servletHandler.addServlet(new ServletHolder(managerServlet), Utils.serverAppDel);
            servletHandler.addServlet(new ServletHolder(managerServlet), Utils.serverAppClear);


            //copy asserts to work path
            extractAssets(mContext.getResources().getAssets(), Utils.CONFIG_ASSETS_DIR);

            //mkdir download dir
            File downloadDir = new File(Utils.configWorkPath+"/"+Utils.CONFIG_DOWNLOAD_DIR);
            if(downloadDir.isFile()) downloadDir.delete();
            if(!downloadDir.exists()) downloadDir.mkdirs();
            Utils.configDownloadPath = downloadDir.getAbsolutePath();

            // setup Resource Handler
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setResourceBase(Utils.configWorkPath);

            HandlerList handlerList = new HandlerList();
            handlerList.addHandler(servletHandler);
            handlerList.addHandler(resourceHandler);
            mServer = new Server(mPort);
            mServer.setHandler(handlerList);
        }

        try {
            mServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * stop server
     */
    public synchronized void stop() {
        if ((mServer == null) || (mServer.isStopped())) {
            return;
        }
        try {
            mServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return true: server is running
     */
    public synchronized boolean isStarted() {
        if (mServer == null) {
            return false;
        }
        return mServer.isStarted();
    }

    private File getWorkPath() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        return sdCardExist ? (Environment.getExternalStorageDirectory()) : (mContext.getFilesDir());
    }


    private void extractAssets(final AssetManager am, final String assetsDir) {
        String[] files = null;
        try {
            if (am != null) {
                files = am.list(assetsDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if ((files == null) || (files.length == 0)) {
            return;
        }

        // extract
        File extractDir = new File(mWorkPathFile.getAbsolutePath() + "/" + assetsDir);
        if (extractDir.isFile()) {
            extractDir.delete();
            return;
        }
        if (!extractDir.exists()) {
            extractDir.mkdirs();
        }

        for (String file : files) {
            InputStream in = null;
            BufferedOutputStream out = null;
            try {
                in = am.open(assetsDir + "/" + file);
                out = new BufferedOutputStream(new FileOutputStream(new File(
                        extractDir, file)));
                byte[] buffer = new byte[4 * 1024]; // 4k
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.close();
                in.close();
            } catch (FileNotFoundException e) {
                extractAssets(am, assetsDir + "/" + file);
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    // ignore
                }
            }

        }
    }
}
