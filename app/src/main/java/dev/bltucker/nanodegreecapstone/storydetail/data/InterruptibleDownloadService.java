package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.StoryCommentsDownloadStartedEvent;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.storydetail.DetailStory;
import timber.log.Timber;


//scope and component for this
//make it completely testable

public class InterruptibleDownloadService extends Service {

    private static final String STORY_PARAM = "dev.bltucker.nanodegreecapstone.storydetail.data.extra.STORY";
    public static final int DOWNLOAD_STORY_COMMENTS_MESSAGE = 1;

    private volatile Looper serviceLooper;
    private volatile ServiceMessageHandler serviceHandler;

    @Nullable
    private volatile StoryCommentDownloadSubscriber currentSubscriber;

    @Inject
    StoryCommentsObservableFactory observableFactory;

    @Inject
    StoryCommentDownloadSubscriberFactory subscriberFactory;

    @Inject
    EventBus eventBus;


    public static void startDownload(Context context, DetailStory story) {
        Intent intent = new Intent(context, InterruptibleDownloadService.class);
        intent.putExtra(STORY_PARAM, story);
        context.startService(intent);
    }

    public InterruptibleDownloadService() {
        DaggerInjector.getApplicationComponent().inject(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //we do not allow binding
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("Service.onCreate");

        HandlerThread handlerThread = new HandlerThread("InterruptibleDownloadServiceHandlerThread");
        handlerThread.start();

        serviceLooper = handlerThread.getLooper();
        serviceHandler = new ServiceMessageHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("Service.onStartCommand");

        //remove any messages that might currently be in queue
        serviceHandler.removeMessages(DOWNLOAD_STORY_COMMENTS_MESSAGE);

        //unsubscribe any currently executing subscriber
        if (currentSubscriber != null && !currentSubscriber.isUnsubscribed()) {
            currentSubscriber.unsubscribe();
        }

        Message msg = serviceHandler.obtainMessage();

        msg.arg1 = startId;
        msg.what = DOWNLOAD_STORY_COMMENTS_MESSAGE;
        msg.setData(intent.getExtras());

        serviceHandler.sendMessage(msg);
        return START_NOT_STICKY;
    }

    /**
     * This method will execute on the handler thread.
     * @param messageData
     */
    public void processMessage(@Nullable Bundle messageData){
        if (null == messageData) {
            Timber.e("Message data for an InterruptibleDownloadService message was null");
            return;
        }

        DetailStory story = messageData.getParcelable(STORY_PARAM);

        if (null == story) {
            Timber.e("DetailStory was missing from message data for InterruptibleDownloadService message");
            return;
        }

        long[] primitiveCommentIds = convertToPrimitiveArray(story.getCommentIds());
        Timber.d("ServiceMessageHandler.handleMessage, story id: %d with %d comments", story.getStoryId(), primitiveCommentIds.length);

        eventBus.publish(new StoryCommentsDownloadStartedEvent());
        currentSubscriber = subscriberFactory.get();
        observableFactory.get(primitiveCommentIds)
                .subscribe(currentSubscriber);
    }

    private long[] convertToPrimitiveArray(Long[] commentIds) {
        long[] primitiveCommentIds = new long[commentIds.length];
        for (int i = 0; i < commentIds.length; i++) {
            primitiveCommentIds[i] = commentIds[i];
        }

        return primitiveCommentIds;
    }

    @Override
    public void onDestroy() {
        Timber.d("Service.onDestroy");
        super.onDestroy();
    }

    private final class ServiceMessageHandler extends Handler {

        ServiceMessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            processMessage(msg.getData());
            stopSelf(msg.arg1);
        }
    }
}
