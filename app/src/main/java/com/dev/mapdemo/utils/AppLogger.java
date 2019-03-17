package com.dev.mapdemo.utils;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class AppLogger {
    private static File logFile;
    private String TAG = AppLogger.class.getSimpleName();

    protected abstract String setDirNameForLog();

    protected abstract String setFileNameForLog();

    protected abstract AppLogger getSingletonObject();

    public AppLogger createLogger() {
        try {
            String root = getStorageFile().toString();
            File myDir = new File(root + "/" + setDirNameForLog() + setFileExtension());
            boolean mkDirs = myDir.mkdirs();
            AppLogs.getInstance().i(TAG, "mkdirs " + mkDirs);
            logFile = new File(myDir, setFileNameForLog());
            if (logFile.exists()) {
                boolean deleteFile = logFile.delete();
                AppLogs.getInstance().i(TAG, "deleteFile " + deleteFile);
            }
            boolean createFile = logFile.createNewFile();
            AppLogs.getInstance().i(TAG, "CreateNewFile " + createFile);
        } catch (IOException e) {
            AppLogs.getInstance().exception(TAG, e);
        }
        return getSingletonObject();
    }

    protected String setFileExtension() {
        return ".txt";
    }

    private File getStorageFile() {
        File directory = null;
        //if there is no SD card, create new directory objects to make directory on device
        if (Environment.getExternalStorageState() == null) {
            //create new file directory object
            directory = new File(Environment.getDataDirectory().toString());
            if (!directory.exists()) {
                directory.mkdir();
            }
            // if phone DOES have sd card
        } else if (Environment.getExternalStorageState() != null) {
            // search for directory on SD card
            directory = new File(Environment.getExternalStorageDirectory().toString());
            if (!directory.exists()) {
                directory.mkdir();
            }
        }// end of SD card checking
        return directory;
    }

    private void writeLog(String message) {
        try {
            if (logFile != null && logFile.exists()) {
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
                out.println(message);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            AppLogs.getInstance().exception(TAG, e);
        }
    }
}
