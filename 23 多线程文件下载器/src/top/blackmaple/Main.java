package top.blackmaple;
import top.blackmaple.core.Downloader;
import top.blackmaple.util.LogUtils;
public class Main {
    public static void main(String[] args) {
        String url = null;
        if (args == null || args.length == 0) {
            do {
                LogUtils.info("请输入下载地址：");
                url = new java.util.Scanner(System.in).nextLine();
            } while (url == null);
        }
        new Downloader().download(url);
    }
}
