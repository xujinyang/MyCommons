package org.liushui.mycommons.android;

import java.lang.Thread.UncaughtExceptionHandler;

import org.liushui.mycommons.android.exception.McException;
import org.liushui.mycommons.android.log.McLog;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * MyCommons全局上下文，使用是需要在App上下文中初始化<br>
 * 比如如下所示：
 * <pre>
 * 直接在AndroidManifest.xml中定义
 * &lt;application
 * ....
 *        android:name="org.liushui.mycommons.android.McApplication"
 * ....
 *        /&gt;
 * 调用方式: McApplication mcApplication = McApplication.getMcAppInstance();
 * </pre>
 * <p/>
 * <pre>
 * 或者继承McApplication
 * public class AppContext extends McApplication&lt;AppContext&gt; {
 *
 * }
 *
 * 调用方式: AppContext appContext = AppContext.getMcAppInstance();
 * </pre>
 * Title: McApplication.java<br>
 * author:xiaqiulei@gmail.com <br>
 * Date: 2013-5-8<br>
 * Version:v1.0
 */
public class McApplication<T extends McApplication<?>> extends Application {
    private static McApplication<?> instance;

    /**
     * 获取实例化对象
     *
     * @return
     */
    public static <F extends McApplication<?>> F getMcAppInstance() {
        if (instance == null) {
            throw new McException("McApplication is null.");
        }
        return (F) instance;
    }

    /**
     * 屏幕的宽度，获取的时候，已经换算过来，width < height
     */
    public static int SCREEN_WIDTH = -1;

    /**
     * 屏幕的高度，获取的时候，已经换算过来，width < height
     */
    public static int SCREEN_HEIGHT = -1;

    /**
     * px和dp的比率
     */
    public static float DIMEN_RATE = -1;

    public static int DIMEN_DPI = -1;

    private Handler handler;

    /*
     * (non-Javadoc)
     *
     * @see
     * android.content.ContextWrapper#attachBaseContext(android.content.Context)
     */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        instance = this;
        handler = new Handler(base.getMainLooper());

        ApplicationInfo info = getApplicationInfo();
        String lable = info.name;
        if (lable == null) {
            int res = info.labelRes;
            if (res != 0) {
                lable = getString(res);
            }
        }
        if (lable == null) {
            lable = getPackageName();
        }
        McLog.i("===============App %s(%s) start================", lable, getPackageName());
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Application#onCreate()
     */
    public void onCreate() {
        super.onCreate();
        McLog.md(this, "onCreate");
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable th) {
                McApplication.this.uncaughtException(thread, th);
            }
        });
        init();
    }

    private void init() {
        int[] size = getScreenSize();
        SCREEN_WIDTH = size[0]; // display.getWidth();
        SCREEN_HEIGHT = size[1]; // display.getHeight();
        if (SCREEN_WIDTH > SCREEN_HEIGHT) {
            int t = SCREEN_HEIGHT;
            SCREEN_HEIGHT = SCREEN_WIDTH;
            SCREEN_WIDTH = t;
        }
        McLog.i("SCREEN_WIDTH = " + SCREEN_WIDTH);
        McLog.i("SCREEN_HEIGHT = " + SCREEN_HEIGHT);
        McLog.i("DIMEN_RATE = " + DIMEN_RATE);
        McLog.i("DIMEN_DPI = " + DIMEN_DPI);
    }

    public int[] getScreenSize() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics dm = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(dm);
        DIMEN_RATE = dm.density / 1.0F;
        DIMEN_DPI = dm.densityDpi;
        return new int[]{dm.widthPixels, dm.heightPixels};
    }

    /**
     * 处理全局未经处理异常
     *
     * @param thread
     * @param th
     */
    protected void uncaughtException(Thread thread, Throwable th) {
        Log.i(getPackageName(), this + "..............uncaughtException");
        Log.e(getPackageName(), thread.toString());
        Log.e(getPackageName(), "Throwable", th);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Application#onTerminate()
     */
    public void onTerminate() {
        super.onTerminate();
        McLog.md(this, "onTerminate");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Application#onLowMemory()
     */
    public void onLowMemory() {
        super.onLowMemory();
        McLog.md(this, "onLowMemory");
    }

    /**
     * (non-Javadoc)
     *
     * @see android.os.Handler#post(Runnable r)
     */
    public void post(Runnable runnable) {
        handler.post(runnable);
    }

    /**
     * (non-Javadoc)
     *
     * @see android.os.Handler#postDelayed(Runnable r, long delayMillis)
     */
    public void postDelayed(Runnable runnable, long delayMillis) {
        handler.postDelayed(runnable, delayMillis);
    }
}