package me.huanmeng.opensource.scheduler;

/**
 * 2023/3/17<br>
 * Gui<br>
 *
 * @author huanmeng_qwq
 */
public class Schedulers {
    private static Scheduler sync, async;

    public static void setSync(Scheduler sync) {
        Schedulers.sync = sync;
    }

    public static void setAsync(Scheduler async) {
        Schedulers.async = async;
    }

    public static Scheduler async() {
        return async;
    }

    public static Scheduler sync() {
        return sync;
    }
}
