package top.blackmaple.util;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Http工具类
 */

public class HttpUtils {

    /**
     * 获取HttpURLConnection
     *
     * @param url 请求地址
     * @return HttpURLConnection
     */
    public static HttpURLConnection getHttpURLConnection(String url) throws Exception {
        URL httpUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
        connection.setRequestMethod("GET");
        // user agent
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        // set timeout 5s
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        return connection;
    }

    /**
     * 获取文件名
     *
     * @param url 请求地址
     * @return 文件名
     */
    public static String getFileName(String url) {
        if (url == null) {
            return null;
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
