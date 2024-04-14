package top.blackmaple.util;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Http工具类
 */

public class HttpUtils {

    /**
     * 获取Http内容长度
     *
     * @param url 请求地址
     * @return 内容长度
     * @throws Exception 异常
     */
    public static long getHttpContentLength(String url) throws Exception {
        HttpURLConnection connection = null;
        long contentLength = 0;
        try {
            connection = getHttpURLConnection(url);
            contentLength = connection.getContentLengthLong();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return contentLength;
    }

    /**
     * 获取HttpURLConnection
     *
     * @param url      请求地址
     * @param startPos 开始位置
     * @param endPos   结束位置
     * @return HttpURLConnection
     * @throws Exception 异常
     */
    public static HttpURLConnection getHttpURLConnection(String url, long startPos, long endPos) throws Exception {
        HttpURLConnection connection = getHttpURLConnection(url);
        String byteRange = "bytes=" + startPos + "-";
        if (endPos != 0) {
            byteRange += endPos;
        }
        connection.setRequestProperty("Range", byteRange);
        return connection;
    }


    /**
     * 获取HttpURLConnection
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
