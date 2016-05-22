package dev.bltucker.nanodegreecapstone.storydetail;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import javax.inject.Provider;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.models.Story;
import timber.log.Timber;

class StoryCommentLoaderCallbackDelegate implements LoaderManager.LoaderCallbacks<List<Comment>> {

    private final Provider<StoryCommentsLoader> commentsLoaderProvider;
    private final ReadingSession readingSession;
    private StoryDetailView storyDetailView;

    public StoryCommentLoaderCallbackDelegate(ReadingSession readingSession, Provider<StoryCommentsLoader> commentsLoaderProvider) {
        this.commentsLoaderProvider = commentsLoaderProvider;
        this.readingSession = readingSession;
    }

    public void setStoryDetailView(StoryDetailView view){
        this.storyDetailView = view;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if(storyDetailView != null){
            storyDetailView.showCommentsLoadingSpinner();
        }
        StoryCommentsLoader commentsLoader = commentsLoaderProvider.get();
        commentsLoader.setStoryId(args.getLong(StoryCommentsLoader.SELECTED_STORY_ID_BUNDLE_KEY));
        return commentsLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Comment>> loader, final List<Comment> data) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (storyDetailView != null) {
                    storyDetailView.hideCommentsLoadingSpinner();
                    storyDetailView.showStory();
                    storyDetailView.showComments(data);
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Timber.d("Loader reset");
    }
}