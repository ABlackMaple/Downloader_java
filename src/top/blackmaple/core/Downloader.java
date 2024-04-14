package top.blackmaple.core;
import top.blackmaple.constant.Constant;
import top.blackmaple.util.FileUtils;
import top.blackmaple.util.HttpUtils;
import top.blackmaple.util.LogUtils;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * 下载器
 */
public class Downloader {
    DownloadInfoThread downloadInfoThread;
    // 线程池
    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            Constant.THREAD_COUNT,
            Constant.THREAD_COUNT,
            0,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(Constant.THREAD_COUNT));

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private CountDownLatch countDownLatch = new CountDownLatch(Constant.THREAD_COUNT);

    public void download(String url) {

        // 下载文件路径
        String httpFileName = Constant.DOWNLOAD_PATH + HttpUtils.getFileName(url);
        System.out.println("保存到" + httpFileName);


        try {
            // 获取连接
            long contentLength = HttpUtils.getHttpContentLength(url);
            downloadInfoThread = new DownloadInfoThread(contentLength);
            scheduledExecutorService.scheduleWithFixedDelay(downloadInfoThread, 0, 1, TimeUnit.SECONDS);
            ArrayList<Future> futures = new ArrayList<>();
            FileUtils.split(url, futures, downloadInfoThread, threadPoolExecutor, countDownLatch);

            // 等待所有线程下载完成
            countDownLatch.await();

        } catch (Exception e) {
            LogUtils.error("下载失败{}", e.getMessage());
        } finally {

            scheduledExecutorService.shutdownNow();
            threadPoolExecutor.shutdown();
            try {
                if (!threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    threadPoolExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPoolExecutor.shutdownNow();
            }
        }

        // 合并文件
        if (FileUtils.mergeFile(httpFileName)) {
            System.out.print("\r");
            System.out.print("任务完成\n");
        } else {
            System.out.print("\r");
            System.out.print("任务失败\n");
        }

    }


}
