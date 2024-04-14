package top.blackmaple.util;

import java.io.File;

public class FileUtils {
    public static long getContentLength(String url) {
        File file = new File(url);
        return file.isFile() ? file.length() : 0;
    }
}
