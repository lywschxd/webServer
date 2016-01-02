package com.dd.webserver.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dd.webserver.R;
import com.dd.webserver.jetty.server.FileServer;
import com.dd.webserver.jetty.server.JServer;
import com.dd.webserver.util.Utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by dong on 2015-12-23 0023.
 */
public class MainActivity extends AppCompatActivity {
    private static final int PORT = 8084;
    private static final int FILE_PORT = 8090;
    private TextView infoTextView;
    private TextView fileTextView;
    private Button conButton;
    private Button fileButton;
    private String mIpAddress;
    private JServer mJServer;
    private FileServer mFileServer;

    private String mInfo = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoTextView = (TextView) findViewById(R.id.text);
        fileTextView = (TextView) findViewById(R.id.fitext);

        conButton = (Button) findViewById(R.id.btn);
        fileButton = (Button) findViewById(R.id.file_btn);
        mInfo = getResources().getString(R.string.internet_addr);
        try {
            mIpAddress = GetIpAddress();
            if(!mIpAddress.equals("")) {
                infoTextView.setText(R.string.has_internet);
                fileTextView.setText(R.string.has_internet);
                Log.d("dddd", "text:"+fileTextView.getText());
                conButton.setEnabled(true);
                fileButton.setEnabled(true);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        mJServer = new JServer(this, PORT);
        mFileServer = new FileServer(this, FILE_PORT);
    }

    @Override
    protected void onDestroy() {
        if(mJServer.isStarted()) mJServer.stop();
        if(mFileServer.isStarted()) mFileServer.stop();
        super.onDestroy();
    }

    public void onButtonStart(View view) {
        if(mJServer.isStarted()) {
            mJServer.stop();
            ((Button)view).setText(R.string.btn_start);
            infoTextView.setText(mIpAddress.isEmpty() ? R.string.no_internet : R.string.has_internet);
        }else {
            mJServer.start();
            ((Button)view).setText(R.string.btn_stop);
            Utils.serverIpPort = mInfo+"http://"+mIpAddress+":"+PORT;
            infoTextView.setText(mInfo+"http://"+mIpAddress+":"+PORT);
        }
    }

    public void onFileStart(View view) {
        if(mFileServer.isStarted()) {
            mFileServer.stop();
            ((Button)view).setText(R.string.btn_file_start);
            fileTextView.setText(mIpAddress.isEmpty() ? R.string.no_internet : R.string.has_internet);
        }else{
            mFileServer.start();
            ((Button)view).setText(R.string.btn_file_stop);
            Utils.serverFileIpPort = mInfo+"http://"+mIpAddress+":"+FILE_PORT;
            fileTextView.setText(mInfo+"http://"+mIpAddress+":"+FILE_PORT);
        }
    }

    /**
     * if (intf.getName().toLowerCase().equals("eth0") || intf.getName().toLowerCase().equals("wlan0"))
     * 表示:仅过滤无线和有线的ip. networkInterface是有很多的名称的
     * 比如sim0,remt1.....等等.我不需要用到就直接过滤了
     * <p/>
     * if (!ipaddress.contains("::"))
     * 表示: 过滤掉ipv6的地址.不管无线还是有线 都有这个地址,
     * 我这边显示地址大体是:fe80::288:88ff:fe00:1%eth0 fe80::ee17:2fff:fece:c0b4%wlan0
     * 一般都是出现在第一次循环.第二次循环就是真正的ipv4的地址.
     *
     * @return
     * @throws SocketException
     */
    private String GetIpAddress() throws SocketException {
        String ipaddress = "";
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface intf = netInterfaces.nextElement();
                if (intf.getName().toLowerCase().equals("eth0") || intf.getName().toLowerCase().equals("wlan0")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            ipaddress = inetAddress.getHostAddress().toString();
                            if (!ipaddress.contains("::")) {// ipV6的地址
                                ipaddress = ipaddress;
                            }
                        }
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ipaddress;
    }
}
