package com.abc.wxctrl.utility;

public class FileUtil {

    public static String get3LevelsPath(String string) {
        return string.replaceFirst("(..)(..)(..).+", "/$1/$2/$3/$0");  
    }
}
