package com.dd.webserver.jetty.server;

import android.content.Context;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

/**
 * Created by dong on 2015-12-29 0029.
 */
public class FileServer {
    private static final String TAG = "FileServer";
    private static final String DEFAULT_DIR = "/mnt/usb/sda1";
    private Context mContext;
    private int mPort = 8090;
    private String mShareFolder = DEFAULT_DIR;
    private Server mServer = null;

    public FileServer(Context mContext) {
        this.mContext = mContext;
    }

    public FileServer(Context mContext, int mPort) {
        this.mContext = mContext;
        this.mPort = mPort;
    }

    public FileServer(Context mContext, String mShareFolder, int mPort) {
        this.mContext = mContext;
        this.mShareFolder = mShareFolder;
        this.mPort = mPort;
    }

    /**
     * start server
     */
    public synchronized void start() {
        if((mServer != null) && (mServer.isStarted())) return;
        if(mServer == null) {
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setDirectoriesListed(true);
            resourceHandler.setResourceBase(mShareFolder);
            resourceHandler.setStylesheet("");

            HandlerList handlerList = new HandlerList();
            handlerList.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });
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
}
