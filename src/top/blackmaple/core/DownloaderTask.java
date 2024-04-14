package top.blackmaple.core;

import top.blackmaple.constant.Constant;
import top.blackmaple.util.HttpUtils;
import top.blackmaple.util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class DownloaderTask implements Callable<Boolean> {

    private final String url;

    private final long startPos;

    private final long endPos;

    private final int threadId;

    DownloadInfoThread downloadInfoThread;

    CountDownLatch countDownLatch;

    public DownloaderTask(String url, long startPos, long endPos, int threadId,
                          DownloadInfoThread downloadInfoThread,
                          CountDownLatch countDownLatch
    ) {
        this.url = url;
        this.startPos = startPos;
        this.endPos = endPos;
        this.threadId = threadId;
        this.downloadInfoThread = downloadInfoThread;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Boolean call() throws Exception {
        String httpFileName = HttpUtils.getFileName(url);
        httpFileName = httpFileName + ".temp" + "_" + threadId;
        // 下载位置
        httpFileName = Constant.DOWNLOAD_PATH + httpFileName;

        // 获取连接
        HttpURLConnection connection = HttpUtils.getHttpURLConnection(url, startPos, endPos);

        try (InputStream input = connection.getInputStream();
             RandomAccessFile accessFile = new RandomAccessFile(httpFileName, "rw");
             BufferedInputStream bufferedInputStream = new BufferedInputStream(input)
        ) {
            byte[] bytes = new byte[Constant.BUFFER_SIZE];
            int len;
            while ((len = bufferedInputStream.read(bytes)) != -1) {
                accessFile.write(bytes, 0, len);
                downloadInfoThread.downSize.add(len);
            }
        } catch (FileNotFoundException e) {
            LogUtils.error("文件不存在{}", e.getMessage());
        } catch (Exception e) {
            LogUtils.error("下载失败{}", e.getMessage());
        } finally {
            connection.disconnect();
        }
        countDownLatch.countDown();

        return true;
    }
}
