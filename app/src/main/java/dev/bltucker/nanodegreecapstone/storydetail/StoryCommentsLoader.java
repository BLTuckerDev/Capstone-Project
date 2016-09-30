package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.CommentRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.events.StoryCommentsDownloadCompleteEvent;
import dev.bltucker.nanodegreecapstone.models.Comment;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class StoryCommentsLoader extends AsyncTaskLoader<Void> {

    static final String SELECTED_DETAIL_STORY = "selectedStoryBundleKey";

    public static final int STORY_COMMENT_LOADER = StoryCommentsLoader.class.hashCode();

    private final CommentRepository commentRepository;
    private final EventBus eventBus;
    private DetailStory detailStory;
    private Subscription syncCompletedSubscription;

    @Inject
    public StoryCommentsLoader(Context context, CommentRepository repository, EventBus eventBus){
        super(context);
        commentRepository = repository;
        this.eventBus = eventBus;
        onContentChanged();
    }

    @Override
    public Void loadInBackground() {

        commentRepository.getStoryComments(detailStory.getStoryId())
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(new Subscriber<List<Comment>>() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) { }

                    @Override
                    public void onNext(List<Comment> comments) {
                        Timber.d("Comments loader onNext");
                        if(isReset()){
                            unsubscribe();
                            return;
                        }
                        detailStory.addComments(comments);
                    }
                });

        return null;
    }

    @Override
    protected void onStartLoading() {
        subscribeToSyncCompleteEvent();

        if (takeContentChanged()) {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        syncCompletedSubscription.unsubscribe();
        super.onReset();
    }

    private void subscribeToSyncCompleteEvent() {
        //TODO pay attention to what story we are downloading here
        syncCompletedSubscription = eventBus.subscribeTo(StoryCommentsDownloadCompleteEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error while listening for comment updates");
                    }

                    @Override
                    public void onNext(Object o) {
                        Timber.d("Story Comment sync completed! Loader will now perform onContentChanged");
                        onContentChanged();
                    }
                });
    }

    public void setDetailStory(DetailStory detailStory) {
        this.detailStory = detailStory;
        onContentChanged();
    }
}
