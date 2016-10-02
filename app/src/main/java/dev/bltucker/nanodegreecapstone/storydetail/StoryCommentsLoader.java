package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.CommentRepository;
import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.models.Comment;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class StoryCommentsLoader extends AsyncTaskLoader<Void> {

    static final String SELECTED_DETAIL_STORY = "selectedStoryBundleKey";

    static final int STORY_COMMENT_LOADER = StoryCommentsLoader.class.hashCode();

    private final CommentRepository commentRepository;
    private DetailStory detailStory;
    private ForceLoadContentObserver myContentObserver;

    @Inject
    public StoryCommentsLoader(Context context, CommentRepository repository){
        super(context);
        commentRepository = repository;
        onContentChanged();
    }

    @Override
    public Void loadInBackground() {

        commentRepository.getStoryComments(detailStory.getStoryId())
                .subscribeOn(Schedulers.immediate())
                .observeOn(AndroidSchedulers.mainThread())
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
        myContentObserver = new ForceLoadContentObserver();
        getContext().getContentResolver().registerContentObserver(SchematicContentProviderGenerator.CommentPaths.ALL_COMMENTS, true, myContentObserver);

        if (takeContentChanged()) {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        getContext().getContentResolver().unregisterContentObserver(myContentObserver);
        super.onReset();
    }

    public void setDetailStory(DetailStory detailStory) {
        this.detailStory = detailStory;
        onContentChanged();
    }
}
