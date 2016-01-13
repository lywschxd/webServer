package com.dd.webserver.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.webserver.R;
import com.dd.webserver.ftp.common.FsSettings;

/**
 * Created by dong on 2016-01-11 0011.
 */
public class FtpSettingsDialog implements DialogInterface.OnClickListener {
    private Context mContext;
    private AlertDialog mAlertDialog;
    private AlertDialog.Builder mBuilder;
    private View mView;

    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText portEditText;
    private EditText pathEditText;
    private CheckBox anonymousCheckBox;

    public FtpSettingsDialog(Context mContext) {
        this.mContext = mContext;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.ftp_setting_dialog, null);

        userNameEditText = (EditText) mView.findViewById(R.id.user_ftp);
        passwordEditText = (EditText) mView.findViewById(R.id.user_password);
        portEditText = (EditText) mView.findViewById(R.id.ftp_prot);
        pathEditText = (EditText) mView.findViewById(R.id.ftp_dir);
        anonymousCheckBox = (CheckBox) mView.findViewById(R.id.anonymous);

        anonymousCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FsSettings.setAllowAnoymous(isChecked);
            }
        });

        userNameEditText.setText(FsSettings.getUserName());
        passwordEditText.setText(FsSettings.getPassWord());
        portEditText.setText(FsSettings.getPortNumber()+"");
        pathEditText.setText(FsSettings.getChrootDirAsString());
        anonymousCheckBox.setChecked(FsSettings.allowAnoymous());

        mBuilder = new AlertDialog.Builder(mContext).setView(mView)
                .setPositiveButton("完成", this);
    }

    public void show() {
        if (mAlertDialog == null) {
            mAlertDialog = mBuilder.show();
        } else {
            mAlertDialog.show();
        }
    }

    public void hide() {
        if (mAlertDialog != null) {
            mAlertDialog.hide();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String userName = userNameEditText.getText().toString();
        if ((!userName.isEmpty()) && (userName.matches("[a-zA-Z0-9]+"))) {
            FsSettings.setUserName(userName);
        } else {
            Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = passwordEditText.getText().toString();
        if ((!password.isEmpty()) && (password.matches("[a-zA-Z0-9]+"))) {
            FsSettings.setPassWord(password);
        } else {
            Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
            return;
        }

        int port = FsSettings.getPortNumber();
        try {
            port = Integer.parseInt(portEditText.getText().toString());
        } catch (Exception e) {
        }
        if (port <= 0 || 65535 < port) {
            Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
            return;
        }
        FsSettings.setPortNumber(port);

        String path = pathEditText.getText().toString();
        if (!path.isEmpty()) {
            FsSettings.setChrootDir(path);
        } else {
            Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
