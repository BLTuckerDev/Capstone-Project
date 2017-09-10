package dev.bltucker.nanodegreecapstone.storydetail;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.daos.ReadLaterStoryDao;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;
import dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentScope;
import io.reactivex.Completable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@StoryDetailFragmentScope
public class StoryDetailViewPresenter {

    private StoryDetailView view;

    private final ReadLaterStoryDao readLaterStoryDao;

    @NonNull
    private final CommentRepository commentRepository;

    @Inject
    public StoryDetailViewPresenter(@NonNull ReadLaterStoryDao readLaterStoryDao,
                                    @NonNull CommentRepository commentRepository) {
        this.readLaterStoryDao = readLaterStoryDao;
        this.commentRepository = commentRepository;
    }


    public void onViewCreated(StoryDetailView detailView, DetailStory detailStory) {
        setDetailView(detailView);
        trackScreenView();

        if (detailStory.hasStory()) {
            view.showStory();
            loadComments(detailStory);
        } else {
            view.showEmptyView();
        }

    }

    public void onViewRestored(StoryDetailView detailView, DetailStory detailStory) {
        setDetailView(detailView);

        if (detailStory.hasStory()) {
            view.showStory();
            loadComments(detailStory);
        } else {
            view.showEmptyView();
        }
    }

    private void loadComments(DetailStory story){
        commentRepository.syncLatestStoryComments(story.getStoryId());
        commentRepository.getCommentsForStoryId(story.getStoryId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Comment[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Comment[] comments) {
                        Log.d("comments", "comments loadded to the presenter: " + comments.length);
                        story.addComments(Arrays.asList(comments));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("comments", "onError" + e.getMessage() + ", " + e.getClass().getSimpleName());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("comments", "onComplete!");
                    }
                });
    }

    private void trackScreenView() {
        //TODO implement with firebase analytics
    }


    private void setDetailView(StoryDetailView detailView) {
        view = detailView;
    }

    public void onViewResumed(StoryDetailView detailView) {
        view = detailView;
    }

    public void onViewPaused() {
        view = null;
    }

    public void onViewDestroyed() {
    }

    public void onReadButtonClicked() {
        if (view != null) {
            view.showStoryPostUrl();
        }
    }

    public void onSaveStoryClick(DetailStory detailStory) {
        if (null == detailStory) {
            return;
        }

        final ReadLaterStory saveMe = new ReadLaterStory(detailStory.getStoryId(), detailStory.getPosterName(), detailStory.getTitle(), detailStory.getUrl());
        Completable.fromAction(() -> readLaterStoryDao.saveStory(saveMe)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    if (view != null) {
                        view.showStorySaveConfirmation();
                    }
                }, e -> Timber.e(e, "Error while attempting to save a read later story."));
    }
}
