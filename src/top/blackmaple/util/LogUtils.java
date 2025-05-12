package top.blackmaple.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志工具类
 */
public class LogUtils {

    /**
     * 打印debug级别日志
     *
     * @param msg  日志信息
     * @param args 参数
     */
    public static void debug(String msg, Object... args) {
        print(msg, "DEBUG", args);
    }

    /**
     * 打印debug级别日志
     *
     * @param msg  日志信息
     * @param args 参数
     */
    public static void error(String msg, Object... args) {
        print(msg, "ERROR", args);
    }


    /**
     * 打印info级别日志
     *
     * @param msg  日志信息
     * @param args 参数
     */
    public static void info(String msg, Object... args) {
        print(msg, "INFO", args);
    }


    /**
     * 打印info级别日志
     *
     * @param msg  日志信息
     * @param args 参数
     */
    private static void print(String msg, String level, Object... args) {
        if (args != null && args.length > 0) {
            msg = String.format(msg.replace("{}", "%s"), args);
        }
        String threadName = Thread.currentThread().getName();
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " [" + threadName + "] " + level + " " + msg);

    }
}
