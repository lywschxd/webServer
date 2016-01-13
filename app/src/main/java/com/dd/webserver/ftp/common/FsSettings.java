/*
Copyright 2011-2013 Pieter Pareit
Copyright 2009 David Revell

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.dd.webserver.ftp.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

public class FsSettings {
    private final static String TAG = FsSettings.class.getSimpleName();
    private final static String FSSETTINGS_USER_NAME = "username";
    private final static String FSSETTINGS_PASSWORD = "password";
    private final static String FSSETTINGS_ALLOW_ANY = "allow_anonymous";
    private final static String FSSETTINGS_PORT_NUM = "portNum";
    private final static String FSSETTINGS_ROOT_DIR = "chrootDir";
    private final static String FSSETTINGS_STAY_AWAKE = "stayAwake";
    private static SharedPreferences mSharedPreferences = null;

    public static void setUserName(String name) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(FSSETTINGS_USER_NAME, name).apply();
    }

    public static String getUserName() {
        final SharedPreferences sp = getSharedPreferences();
        return sp.getString(FSSETTINGS_USER_NAME, "ftp");
    }

    public static void setPassWord(String passWord) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(FSSETTINGS_PASSWORD, passWord).apply();
    }

    public static String getPassWord() {
        final SharedPreferences sp = getSharedPreferences();
        return sp.getString(FSSETTINGS_PASSWORD, "ftp");
    }

    public static void setAllowAnoymous(boolean allow) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(FSSETTINGS_PASSWORD, allow).apply();
    }

    public static boolean allowAnoymous() {
        final SharedPreferences sp = getSharedPreferences();
        return sp.getBoolean(FSSETTINGS_ALLOW_ANY, false);
    }

    public static File getChrootDir() {
        final SharedPreferences sp = getSharedPreferences();
        String dirName = sp.getString(FSSETTINGS_ROOT_DIR, "");
        File chrootDir = new File(dirName);
        if (dirName.equals("")) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                chrootDir = Environment.getExternalStorageDirectory();
            } else {
                chrootDir = new File("/");
            }
        }
        if (!chrootDir.isDirectory()) {
            Log.e(TAG, "getChrootDir: not a directory");
            return null;
        }
        return chrootDir;
    }

    public static String getChrootDirAsString() {
        File dirFile = getChrootDir();
        return dirFile != null ? dirFile.getAbsolutePath() : "";
    }

    public static boolean setChrootDir(String dir) {
        File chrootTest = new File(dir);
        if (!chrootTest.isDirectory() || !chrootTest.canRead())
                return false;
        final SharedPreferences sp = getSharedPreferences();
        sp.edit().putString(FSSETTINGS_ROOT_DIR, dir).apply();
        return true;
    }

    public static void setPortNumber(int port) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(FSSETTINGS_PORT_NUM, port).apply();
    }

    public static int getPortNumber() {
        final SharedPreferences sp = getSharedPreferences();
        // TODO: port is always an number, so store this accordenly
        int port = sp.getInt(FSSETTINGS_PORT_NUM, 2121);
        Log.v(TAG, "Using port: " + port);
        return port;
    }

    public static void setTakeFullWakeLock(boolean wake) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(FSSETTINGS_STAY_AWAKE, wake).apply();
    }

    public static boolean shouldTakeFullWakeLock() {
        final SharedPreferences sp = getSharedPreferences();
        return sp.getBoolean(FSSETTINGS_STAY_AWAKE, false);
    }

    /**
     * @return the SharedPreferences for this application
     */
    private static SharedPreferences getSharedPreferences() {
        Context context = FsApp.getAppContext();
        if(mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("ftp", Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    // cleaning up after his
    protected static int inputBufferSize = 256;
    protected static boolean allowOverwrite = false;
    protected static int dataChunkSize = 8192; // do file I/O in 8k chunks
    protected static int sessionMonitorScrollBack = 10;
    protected static int serverLogScrollBack = 10;

    public static int getInputBufferSize() {
        return inputBufferSize;
    }

    public static void setInputBufferSize(int inputBufferSize) {
        FsSettings.inputBufferSize = inputBufferSize;
    }

    public static boolean isAllowOverwrite() {
        return allowOverwrite;
    }

    public static void setAllowOverwrite(boolean allowOverwrite) {
        FsSettings.allowOverwrite = allowOverwrite;
    }

    public static int getDataChunkSize() {
        return dataChunkSize;
    }

    public static void setDataChunkSize(int dataChunkSize) {
        FsSettings.dataChunkSize = dataChunkSize;
    }

    public static int getSessionMonitorScrollBack() {
        return sessionMonitorScrollBack;
    }

    public static void setSessionMonitorScrollBack(int sessionMonitorScrollBack) {
        FsSettings.sessionMonitorScrollBack = sessionMonitorScrollBack;
    }

    public static int getServerLogScrollBack() {
        return serverLogScrollBack;
    }

    public static void setLogScrollBack(int serverLogScrollBack) {
        FsSettings.serverLogScrollBack = serverLogScrollBack;
    }

}
