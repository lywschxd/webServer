package com.dd.webserver.jetty.servlet;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.dd.webserver.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by dong on 2015-12-23 0023.
 */
public class UploadApkServlet extends HttpServlet {
    private static final String TAG="UploadApkServlet";
    private static final String downAppName = "/download.apk";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

        if(!Utils.isConfigDownloadPathVaild()) {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("text/html");
            resp.getWriter().println(Utils.getErrorInfo("File upload Fail", "invalid work path"));
        }

		InputStream in = req.getInputStream();
		File file = new File(Utils.configDownloadPath+downAppName);
        if(file.exists()) {
            file.delete();
        }

		OutputStream out = new FileOutputStream(file);
		
		// Read the first 8192 bytes.
        // The full header should fit in here.
        // Apache's default header limit is 8KB.
        // Do NOT assume that a single read will get the entire header at once!
        final int bufsize = 8192;
        byte[] buf = new byte[bufsize];
        int splitbyte = 0;
        int rlen = 0;
        {
            int read = in.read(buf, 0, bufsize);
            while (read > 0)
            {
                rlen += read;
                splitbyte = findHeaderEnd(buf, rlen);
                if (splitbyte > 0)
                    break;
                read = in.read(buf, rlen, bufsize - rlen);
            }
        }
        
        if (splitbyte < rlen)
            out.write(buf, splitbyte, rlen-splitbyte);
        
        // While Firefox sends on the first read all the data fitting
        // our buffer, Chrome and Opera send only the headers even if
        // there is data for the body. We do some magic here to find
        // out whether we have already consumed part of body, if we
        // have reached the end of the data to be sent or we should
        // expect the first byte of the body at the next read.
        long size = 0x7FFFFFFFFFFFFFFFl;
        if (splitbyte < rlen)
            size -= rlen-splitbyte+1;
        else if (splitbyte==0 || size == 0x7FFFFFFFFFFFFFFFl)
            size = 0;
        
        // Now read all the body and write it to f
        buf = new byte[1024];
        while ( rlen >= 0 && size > 0 )
        {
            rlen = in.read(buf, 0, 512);
            size -= rlen;
            if (rlen > 0)
                out.write(buf, 0, rlen);
        }
		in.close();
		out.close();

        //only use xgimi device
        xgimiDeviceSilentInstall();
	}
	
    /**
     * Find byte index separating header from body.
     * It must be the last byte of the first two sequential new lines.
    **/
    private int findHeaderEnd(final byte[] buf, int rlen)
    {
        int splitbyte = 0;
        while (splitbyte + 3 < rlen)
        {
            if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n' && buf[splitbyte + 2] == '\r' && buf[splitbyte + 3] == '\n')
                return splitbyte + 4;
            splitbyte++;
        }
        return 0;
    }

    private void xgimiDeviceSilentInstall() {
        Intent intent = new Intent("com.xgimi.settings.APPINSTALL");
        intent.putExtra("package_Path", Utils.configDownloadPath+downAppName);
        Utils.JServerContext.sendBroadcast(intent);
    }
}
