package dev.bltucker.nanodegreecapstone.topstories;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import dev.bltucker.nanodegreecapstone.data.StoryCommentsLoader;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;

class StoryCommentLoaderCallbackDelegate implements LoaderManager.LoaderCallbacks {

    private final StoryCommentsLoader commentsLoader;
    private final ReadingSession readingSession;
    private StoryListView storyListView;

    public StoryCommentLoaderCallbackDelegate(ReadingSession readingSession, StoryCommentsLoader commentsLoader) {
        this.commentsLoader = commentsLoader;
        this.readingSession = readingSession;
    }

    public void setStoryListView(StoryListView view){
        this.storyListView = view;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        commentsLoader.setStory((Story) args.getParcelable(StoryListViewPresenter.SELECTED_STORY_BUNDLE_KEY));
        return commentsLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
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
    public void onLoaderReset(Loader loader) { }
}