package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.StoryProvider;
import dev.bltucker.nanodegreecapstone.data.CommentRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.storydetail.events.StoryCommentsDownloadCompleteEvent;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class StoryCommentsLoader extends AsyncTaskLoader<List<Comment>> {

    public static final String SELECTED_DETAIL_STORY = "selectedStoryBundleKey";

    public static final int STORY_COMMENT_LOADER = StoryCommentsLoader.class.hashCode();

    private long detailStoryId;
    private final CommentRepository commentRepository;
    private final EventBus eventBus;

    private ModulatedForceLoadContentObserver myContentObserver;
    private Disposable downloadCompleteEventSubscription;

    @Inject
    public StoryCommentsLoader(Context context, CommentRepository repository, EventBus eventBus){
        super(context);
        commentRepository = repository;
        this.eventBus = eventBus;
        //TODO injection of the change observer factory
        final int changeModulus = 5;
        myContentObserver = new ModulatedForceLoadContentObserver(changeModulus);
        onContentChanged();
    }

    @Override
    public List<Comment> loadInBackground() {
        Timber.d("StoryCommentsLoader.loadInBackground");
        List<Comment> comments = commentRepository.getStoryComments(detailStoryId).blockingFirst();
        Timber.d("StoryCommentsLoader loaded %d comments", comments.size());
        return comments;
    }

    @Override
    protected void onStartLoading() {
        Timber.d("StoryCommentsLoader.onStartLoading");
        getContext().getContentResolver().registerContentObserver(StoryProvider.COMMENTS_URI, true, myContentObserver);

        eventBus.subscribeTo(StoryCommentsDownloadCompleteEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {

                    @Override
                    public void onError(Throwable e) {
                        Timber.d(e, "Error while processing a StoryCommentsDownloadCompleteEvent");
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        downloadCompleteEventSubscription = d;
                    }

                    @Override
                    public void onNext(Object o) {
                        onContentChanged();
                    }
                });

        if (takeContentChanged()) {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(List<Comment> data) {
        super.deliverResult(data);
        myContentObserver.stopPreventingAdditionalChanges();
    }

    @Override
    public void onCanceled(List<Comment> data) {
        Timber.d("StoryCommentsLoader.onCanceled");
        super.onCanceled(data);
    }


    @Override
    protected void onReset() {
        Timber.d("StoryCommentsLoader.onReset");
        getContext().getContentResolver().unregisterContentObserver(myContentObserver);
        myContentObserver.stopPreventingAdditionalChanges();
        if(downloadCompleteEventSubscription != null && !downloadCompleteEventSubscription.isDisposed()){
            downloadCompleteEventSubscription.dispose();
        }
        super.onReset();
    }

    public void setDetailStoryId(long detailStoryId) {
        this.detailStoryId = detailStoryId;
        onContentChanged();
    }


    private class ModulatedForceLoadContentObserver extends ContentObserver {

        private final int modulus;
        private int changeCount = 0;
        boolean preventAdditionalChanges = false;

        ModulatedForceLoadContentObserver(int modulus) {
            super(new Handler());
            this.modulus = modulus;
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            if(changeCount % modulus == 0 && !preventAdditionalChanges){
              preventAdditionalChanges = true;
              onContentChanged();
            }
            changeCount++;
        }

        void stopPreventingAdditionalChanges(){
            preventAdditionalChanges = false;
        }
    }

}
