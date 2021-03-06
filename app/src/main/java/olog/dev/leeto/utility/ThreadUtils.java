package olog.dev.leeto.utility;

import android.os.Looper;

import olog.dev.leeto.BuildConfig;

public class ThreadUtils {

    private ThreadUtils() {
        //no instance
    }

    public static void assertMainThread(){
        if (BuildConfig.DEBUG){
            boolean isMainThread = isMainThread();
            if(!isMainThread) {
                throw new RuntimeException("not on main thread: " + Thread.currentThread());
            }
        }
    }

    public static void assertBackGroundThread(){
        if (BuildConfig.DEBUG){
            boolean isBackgroundThread = !isMainThread();
            if(!isBackgroundThread) {
                throw new RuntimeException("not on worker thread: " + Thread.currentThread());
            }
        }
    }

    private static boolean isMainThread(){
        // oppure Looper.getMainLooper().getThread() == Thread.currentThread()
        return Looper.myLooper() == Looper.getMainLooper();
    }

}
