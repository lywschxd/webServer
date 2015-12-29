package com.dd.webserver.jetty.servlet;

import android.util.Log;

import com.dd.webserver.util.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ScreenCapServlet extends HttpServlet {
    private static final String TAG = "ScreenCapServlet";
    private static final String PIC_DIR = Utils.configWorkPath+"/pic/";
    private static final String PIC_NAME = "name";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO Auto-generated method stub

        //Do not implement screenshots, return parameter to the name of the image
        boolean hasNameParam = false;

        Enumeration<String> params = req.getParameterNames();
        while (params.hasMoreElements()) {
            if (params.nextElement().toString().equals(PIC_NAME)) {
                hasNameParam = true;
                break;
            }
        }

        if (hasNameParam) {
            String name = req.getParameter(PIC_NAME);
            if(name == null || name.isEmpty()) {
                resp.getWriter().println(getJsonMsg("parameter is invaild!", "", 1));
            }else {
                File pic = new File(PIC_DIR+name);
                Log.d("dddddd", pic.getAbsolutePath());
                if(!pic.exists()) {
                    resp.getWriter().println(getJsonMsg("no picture found!", "", 1));
                }else {
                    File picHtml = new File(PIC_DIR+"pic.html");
                    if(picHtml.exists()) picHtml.delete();
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(picHtml));
                    out.write(getPicHtml(PIC_DIR+name).getBytes("utf-8"));
                    out.close();

                    resp.getWriter().println(getJsonMsg("succeed", "./pic/pic.html", 0));
                }
            }
        } else {
            resp.getWriter().println(getJsonMsg("No parameter called \\\"name\\\"! ", "", 1));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    String getPicHtml(String pic_name) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<title>").append("图片").append("-").append(pic_name.substring(pic_name.lastIndexOf("/")+1, pic_name.length())).append("</title>");
        sb.append(" <meta charset=\"UTF-8\">");
        sb.append("</head>");

        sb.append("<body>");
        sb.append("<img src=\"").append("./").append(pic_name.substring(pic_name.lastIndexOf("/")+1, pic_name.length())).append("\"/>");
        sb.append("</body>");

        sb.append("</html>");


        return sb.toString();
    }

    String getJsonMsg(String msg, String url, int suc) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"msg\":").append("\"").append(msg).append("\"").append(",");
        sb.append("\"url\": ").append("\"").append(url).append("\"").append(",");
        sb.append("\"suc\":").append(suc);
        sb.append("}");
        return sb.toString();
    }
}
