package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.storydetail.DetailStory;
import rx.Subscription;
import timber.log.Timber;

public class StoryCommentDownloadService extends IntentService {

    private static final String STORY_PARAM = "dev.bltucker.nanodegreecapstone.storydetail.data.extra.STORY";

    @Inject
    StoryCommentsObservableFactory observableFactory;

    @Inject
    StoryCommentDownloadSubscriberFactory subscriberFactory;

    private Subscription currentSubscription;

    public StoryCommentDownloadService() {
        super("StoryCommentDownloadService");
        DaggerInjector.getApplicationComponent().inject(this);
    }

    public static void startDownload(Context context, DetailStory story) {
        Intent intent = new Intent(context, StoryCommentDownloadService.class);
        intent.putExtra(STORY_PARAM, story);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final DetailStory story = intent.getParcelableExtra(STORY_PARAM);
            long[] primitiveCommentIds =convertToPrimitiveArray(story.getCommentIds());

            Timber.d("StoryCommentDownloadService.onHandleIntent, story id: %d with %d comments",story.getStoryId(), primitiveCommentIds.length);
//TODO intent service's already make sure only one job happens at a time so this isn't necessary, but we do need a way to stop a job in progress for cases where the user changes stories quickly.
            if(currentSubscription != null && !currentSubscription.isUnsubscribed()){
                Timber.d("StoryCommentDownloadService previous subscription was in progress. It will be unsubscribed");
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
