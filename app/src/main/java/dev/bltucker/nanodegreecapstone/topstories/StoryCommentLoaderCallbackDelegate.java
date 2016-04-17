package dev.bltucker.nanodegreecapstone.topstories;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import javax.inject.Provider;

import dev.bltucker.nanodegreecapstone.data.StoryCommentsLoader;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;
import timber.log.Timber;

class StoryCommentLoaderCallbackDelegate implements LoaderManager.LoaderCallbacks {

    private final Provider<StoryCommentsLoader> commentsLoaderProvider;
    private final ReadingSession readingSession;
    private StoryListView storyListView;

    public StoryCommentLoaderCallbackDelegate(ReadingSession readingSession, Provider<StoryCommentsLoader> commentsLoaderProvider) {
        this.commentsLoaderProvider = commentsLoaderProvider;
        this.readingSession = readingSession;
    }

    public void setStoryListView(StoryListView view){
        this.storyListView = view;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        StoryCommentsLoader commentsLoader = commentsLoaderProvider.get();
        commentsLoader.setStory((Story) args.getParcelable(StoryListViewPresenter.SELECTED_STORY_BUNDLE_KEY));
        return commentsLoader;
//        return commentsLoaderProvider;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        StoryCommentsLoader commentsLoader = (StoryCommentsLoader) loader;
        readingSession.read(commentsLoader.getStory(), (List<Comment>) data);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (storyListView != null) {
                    storyListView.hideLoadingView();
                    storyListView.showCommentsView();
                }
            }
        });

    }

    @Override
    public void onLoaderReset(Loader loader) {
        Timber.d("Loader reset");
    }
}