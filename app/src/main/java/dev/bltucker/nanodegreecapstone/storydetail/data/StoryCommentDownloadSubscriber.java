package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.support.annotation.VisibleForTesting;

import java.util.List;

import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.StoryCommentsDownloadCompleteEvent;
import dev.bltucker.nanodegreecapstone.models.Comment;
import rx.Subscriber;
import timber.log.Timber;

class StoryCommentDownloadSubscriber extends Subscriber<List<Comment>> {

    @VisibleForTesting
    ContentResolver contentResolver;

    @VisibleForTesting
    EventBus eventBus;

    StoryCommentDownloadSubscriber(ContentResolver contentResolver, EventBus eventBus){
        this.contentResolver = contentResolver;
        this.eventBus = eventBus;
    }

    @Override
    public void onCompleted() {
        Timber.d("StoryCommentDownloadSubscriberFactory onComplete()");
        eventBus.publish(new StoryCommentsDownloadCompleteEvent());
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "StoryCommentDownloadSubscriberFactory error");
    }

    @Override
    public void onNext(List<Comment> comments) {
        ContentValues[] contentValues = new ContentValues[comments.size()];

        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            contentValues[i] = Comment.mapToContentValues(comment);
        }

        contentResolver.bulkInsert(SchematicContentProviderGenerator.CommentPaths.ALL_COMMENTS, contentValues);
    }
}
