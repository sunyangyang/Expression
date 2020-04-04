package com.syy.expression.utils;

import android.os.Environment;

import java.io.File;

public class FileUtil {
    public static String getFileExternalPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        if (sdDir != null) {
            String path = sdDir.getAbsolutePath() + "/expression/";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            return path;
        }
        return "expression/";
    }
}
