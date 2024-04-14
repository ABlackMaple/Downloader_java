package top.blackmaple.core;

import top.blackmaple.constant.Constant;
import top.blackmaple.util.FileUtils;
import top.blackmaple.util.HttpUtils;
import top.blackmaple.util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 下载器
 */
public class Downloader {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void download(String url) {

        // 下载文件路径
        String httpFileName = Constant.DOWNLOAD_PATH + HttpUtils.getFileName(url);
        System.out.println("保存到" + httpFileName);

        // 判断文件是否存在
        long localContentLength = FileUtils.getContentLength(httpFileName);

        DownloadInfoThread downloadInfoThread = null;
        // 获取连接
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = HttpUtils.getHttpURLConnection(url);
            long httpContentLength = httpURLConnection.getContentLengthLong();
            downloadInfoThread = new DownloadInfoThread(httpContentLength);
            if (localContentLength == httpContentLength) {
                LogUtils.info("{}已存在", httpFileName);
                return;
            }
            // 开启线程
            scheduledExecutorService.scheduleWithFixedDelay(downloadInfoThread, 0, 1, TimeUnit.SECONDS);

        } catch (Exception e) {
            LogUtils.error("获取连接失败{}" + e.getMessage());
        }

        try {
            assert httpURLConnection != null;
            try (InputStream inputStream = httpURLConnection.getInputStream();
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(httpFileName))
            ) {

                // 下载文件
                int len;
                byte[] bytes = new byte[Constant.BUFFER_SIZE];
                while ((len = bufferedInputStream.read(bytes)) != -1) {
                    assert downloadInfoThread != null;
                    downloadInfoThread.downSize += len;
                    bufferedOutputStream.write(bytes, 0, len);
                }
            }
        } catch (FileNotFoundException e) {
            LogUtils.error("文件不存在{}", e.getMessage());
        } catch (Exception e) {
            LogUtils.error("下载失败{}", e.getMessage());
        } finally {

            // 关闭连接
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();

            }
            scheduledExecutorService.shutdownNow();
        }
        System.out.print("\r");
        System.out.print("\r");
        System.out.print("任务完成\n");

    }
}
