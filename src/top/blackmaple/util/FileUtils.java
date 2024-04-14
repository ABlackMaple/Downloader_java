package top.blackmaple.util;

import top.blackmaple.constant.Constant;
import top.blackmaple.core.DownloadInfoThread;
import top.blackmaple.core.DownloaderTask;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class FileUtils {
    public static long getContentLength(String url) {
        File file = new File(url);
        return file.isFile() ? file.length() : 0;
    }

    public static void split(String url,
                             ArrayList<Future> futures,
                             DownloadInfoThread downloadInfoThread,
                             ThreadPoolExecutor threadPoolExecutor,
                             CountDownLatch countDownLatch) {
        try {
            long contentLength = HttpUtils.getHttpContentLength(url);
            long threadSize = contentLength / Constant.THREAD_COUNT;
            for (int i = 0; i < Constant.THREAD_COUNT; i++) {
                long startPos = i * threadSize;
                long endPos = (i + 1) * threadSize - 1;
                if (i == Constant.THREAD_COUNT - 1) {
                    endPos = 0;
                }
                DownloaderTask downloaderTask = new DownloaderTask(url, startPos, endPos, i, downloadInfoThread, countDownLatch);
                Future<Boolean> submit = threadPoolExecutor.submit(downloaderTask);
                futures.add(submit);
            }

        } catch (Exception e) {
            LogUtils.error("获取文件长度失败{}", e.getMessage());
        }
    }

    /**
     * 合并文件
     *
     * @param url 文件路径
     * @return 是否合并成功
     */
    public static boolean mergeFile(String url) {
        byte[] bytes = new byte[Constant.BUFFER_SIZE];
        int len;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(url, "rw")
        ) {
            for (int i = 0; i < Constant.THREAD_COUNT; i++) {
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(url + ".temp" + "_" + i))) {
                    while ((len = bufferedInputStream.read(bytes)) != -1) {
                        randomAccessFile.write(bytes, 0, len);
                    }
                } catch (IOException e) {
                    LogUtils.error("合并文件失败{}", e.getMessage());
                    return false;
                }
            }
        } catch (IOException e) {
            LogUtils.error("合并文件失败{}", e.getMessage());
            return false;
        }
        return deleteTempFile(url);
    }

    /**
     * 删除临时文件
     *
     * @param url 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteTempFile(String url) {
        for (int i = 0; i < Constant.THREAD_COUNT; i++) {
            File file = new File(url + ".temp" + "_" + i);
            if (!file.delete()) {
                LogUtils.error("删除临时文件失败{}", file.getName());
                return false;
            }
        }
        return true;
    }
}
