package dev.bltucker.nanodegreecapstone.data;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.concurrent.CountDownLatch;

public class TestContentObserver extends ContentObserver {

    public static TestContentObserver createInstance(CountDownLatch countDownLatch, Context context) {
        final HandlerThread ht = new HandlerThread("ContentObserverThread");
        ht.start();
        Handler handler = new Handler(ht.getLooper());
        TestContentObserver testContentObserver = new TestContentObserver(context, ht, handler, countDownLatch);

        return testContentObserver;
    }

    private final Context context;
    private final HandlerThread ht;
    private final Handler handler;
    private final CountDownLatch countDownLatch;

    public TestContentObserver(Context context, HandlerThread ht, Handler handler, CountDownLatch countDownLatch) {
        super(handler);
        this.context = context;
        this.ht = ht;
        this.handler = handler;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        this.countDownLatch.countDown();
        if (this.countDownLatch.getCount() == 0) {
            this.ht.quit();
            this.context.getContentResolver().unregisterContentObserver(this);
        }
    }
}
