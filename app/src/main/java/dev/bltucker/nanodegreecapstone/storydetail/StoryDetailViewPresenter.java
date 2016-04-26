package dev.bltucker.nanodegreecapstone.storydetail;

import dev.bltucker.nanodegreecapstone.models.ReadingSession;

public class StoryDetailViewPresenter {

    private final ReadingSession readingSession;

    private StoryDetailView view;

    public StoryDetailViewPresenter(ReadingSession readingSession){
        this.readingSession = readingSession;
    }


    public void onViewCreated(StoryDetailView detailView) {
        view = detailView;
        view.showStory();
        view.showComments();
    }

    public void onViewRestored(StoryDetailView detailView) {
        view = detailView;
        view.showStory();
        view.showComments();
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
        if(view != null){
            view.showStoryPostUrl(readingSession.getCurrentStory().getUrl());
        }
    }
}
