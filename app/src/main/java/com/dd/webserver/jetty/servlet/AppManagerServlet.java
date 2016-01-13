package com.dd.webserver.jetty.servlet;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.dd.webserver.jetty.common.PackageManager;
import com.dd.webserver.util.AppInfo;
import com.dd.webserver.util.Utils;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by dong on 2015-12-30 0030.
 */
public class AppManagerServlet extends HttpServlet {
    private Context context;
    private ArrayList<AppInfo> AppLists;

    public AppManagerServlet(Context context) {
        this.context = context;
        PackageManager packageManager = PackageManager.getInstance(context);
        AppLists = packageManager.getAppList();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        Log.d("ddddd", "getRequestURI:" + uri);

        if (uri.equals(Utils.serverAppList)) {
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(getAppListJsonMsg(false, AppLists, 0));
        }else if(uri.equals(Utils.serverAppRun)) {
            String parm = req.getParameter("pn");
            if(parm == null || parm.isEmpty()) {
                resp.getWriter().println(getAppManagerJsonMsg("parm error!", 1));
            }else {
                luanchApk(parm);
                resp.getWriter().println(getAppManagerJsonMsg("succeed!", 0));
            }
        }else if(uri.equals(Utils.serverAppStop)) {

        }else if(uri.equals(Utils.serverAppDel)) {
            String parm = req.getParameter("pn");
            if(parm == null || parm.isEmpty()) {
                resp.getWriter().println(getAppManagerJsonMsg("parm error!", 1));
            }else {
                unInstallApK(parm);
                resp.getWriter().println(getAppManagerJsonMsg("succeed!", 0));
            }
        }else if(uri.equals(Utils.serverAppClear)) {

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    private void unInstallApK(String pn) {
        Uri uri = Uri.parse("package:"+pn);//获取删除包名的URI
        Intent i = new Intent();
        i.setAction(Intent.ACTION_DELETE);//设置我们要执行的卸载动作
        i.setData(uri);//设置获取到的URI
        context.startActivity(i);
    }

    private void luanchApk(String pn)  {
        android.content.pm.PackageManager pm = context.getPackageManager();
        Intent i = pm.getLaunchIntentForPackage(pn);//获取启动的包名
        context.startActivity(i);
    }

    private String getAppListJsonMsg(boolean isadb, ArrayList<AppInfo> lists, int suc) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"isadb\": ").append(isadb).append(",");
        sb.append("\"suc\":").append(suc).append(",");
        sb.append("\"list\":");
        sb.append(" [");

        int length = lists.size();
        for (int i = 0; i < length; i++) {
            sb.append("{\"appname\": \"").append(lists.get(i).appName).append("\",");
            sb.append(" \"packagename\": \"").append(lists.get(i).packageName).append("\",");
            sb.append("\"img\": \"").append("./app/" + lists.get(i).packageName + ".png").append("\",");
            sb.append("\"issystem\": ").append(lists.get(i).isSystem).append(",");
            sb.append(" \"isrun\": ").append(isadb).append("}");
            if (i != length - 1) {
                sb.append(",");
            }
        }

        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    private String getAppManagerJsonMsg(String msg, int suc) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"suc\": ").append(suc).append(",");
        sb.append("\"msg\": \"").append(msg).append("\"");
        sb.append("}");
        return sb.toString();
    }

}
