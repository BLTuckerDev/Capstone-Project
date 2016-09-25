package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Subscription;

public class StoryCommentDownloadService extends IntentService {

    private static final String STORY_PARAM = "dev.bltucker.nanodegreecapstone.storydetail.data.extra.STORY";

    @Inject
    StoryCommentsObservableFactory observableFactory;

    @Inject
    StoryCommentDownloadSubscriberFactory subscriberFactory;

    private Subscription currentSubscription;

    public StoryCommentDownloadService() {
        super("StoryCommentDownloadService");
        CapstoneApplication.getApplication().getApplicationComponent().inject(this);
    }

    public static void startDownload(Context context, Story story) {
        Intent intent = new Intent(context, StoryCommentDownloadService.class);
        intent.putExtra(STORY_PARAM, story);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final Story story = intent.getParcelableExtra(STORY_PARAM);
            long[] primitiveCommentIds =convertToPrimitiveArray(story.getCommentIds());

            if(currentSubscription != null && !currentSubscription.isUnsubscribed()){
                currentSubscription.unsubscribe();
            }

            currentSubscription = observableFactory.get(primitiveCommentIds)
                    .subscribe(subscriberFactory.get());
        }
    }

    private long[] convertToPrimitiveArray(Long[] commentIds) {
        long[] primitiveCommentIds = new long[commentIds.length];
        for (int i = 0; i < commentIds.length; i++) {
            primitiveCommentIds[i] = commentIds[i];
        }

        return primitiveCommentIds;
    }
}
