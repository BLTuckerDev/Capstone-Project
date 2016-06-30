package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class StoryCommentsLoader extends AsyncTaskLoader<Void> {

    static final String SELECTED_DETAIL_STORY = "selectedStoryBundleKey";


    public static final int STORY_COMMENT_LOADER = StoryCommentsLoader.class.hashCode();
    private final StoryRepository storyRepository;
    private DetailStory detailStory;

    @Inject
    public StoryCommentsLoader(Context context, StoryRepository repository){
        super(context);
        storyRepository = repository;
        onContentChanged();
    }

    @Override
    public Void loadInBackground() {

        storyRepository.getStoryComments(detailStory.getStoryId())
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
        if (takeContentChanged()) {
            forceLoad();
        }
    }

    public void setDetailStory(DetailStory detailStory) {
        this.detailStory = detailStory;
        onContentChanged();
    }
}
