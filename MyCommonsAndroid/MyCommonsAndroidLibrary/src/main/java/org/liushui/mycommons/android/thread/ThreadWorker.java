package org.liushui.mycommons.android.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.liushui.mycommons.android.log.McLog;

public class ThreadWorker {

    static ExecutorService executorService;

    public static void execute(Runnable runnable) {
        submit(runnable);
    }

    public static Future<?> submit(Runnable runnable) {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(8);
        }
        try {
            Future<?> f = executorService.submit(runnable);
            return f;
        } catch (Exception e) {
            McLog.e("Threadwork exception", e);
        }
        return null;
    }
}