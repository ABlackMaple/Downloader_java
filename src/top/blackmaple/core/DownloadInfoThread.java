package top.blackmaple.core;

import top.blackmaple.constant.Constant;

import java.util.concurrent.atomic.LongAdder;

public class DownloadInfoThread implements Runnable {

    // 下载文件大小
    private final long httpFileContentLength;

    // 前1s内累计下载大小
    public double lastSize;

    // 当前1s内累计下载大小
    public LongAdder downSize;

    public DownloadInfoThread(long httpFileContentLength) {
        this.httpFileContentLength = httpFileContentLength;
        downSize = new LongAdder();
        lastSize = 0;

    }

    @Override
    public void run() {
        // 文件总大小
        String httpFileSize = String.format("%.2f", this.httpFileContentLength / Constant.MB);

        // 下载速度
        int speed = (int) ((this.downSize.doubleValue() - this.lastSize) / 1024);
        // 更新下载进度
        this.lastSize = this.downSize.doubleValue();

        // 剩余文件大小
        double remainSize = this.httpFileContentLength - this.downSize.doubleValue();

        // 剩余时间
        String remainTime = String.format("%.1f", remainSize / speed / 1024);

        if ("Infinity".equals(remainTime)) {
            remainTime = "--";
        }

        // 下载进度
        String progress = String.format("%.2f", (this.downSize.doubleValue()) / Constant.MB);

        // 下载进度百分比
        String percent = String.format("%.2f", (this.downSize.doubleValue()) / this.httpFileContentLength * 100);

        // 下载进度条
        StringBuilder progressBar = new StringBuilder();
        int percentInt = (int) (Double.parseDouble(percent));
        for (int i = 0; i < 50; i++) {
            if (i < percentInt / 2) {
                progressBar.append("=");
            } else {
                progressBar.append(" ");
            }
        }
        System.out.print("\r");
        System.out.print("[" + progressBar + "] " + percent + "%    ");
        System.out.print("文件大小：" + httpFileSize + "MB, 下载进度：" + progress + "MB, 下载速度：" + speed + "KB/s, 剩余时间：" + remainTime + "s");

    }
}
