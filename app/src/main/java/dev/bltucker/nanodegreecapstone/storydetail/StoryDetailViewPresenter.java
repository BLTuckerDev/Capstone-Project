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
        view.showStory(readingSession.getCurrentStory());
        view.showComments(readingSession.getCurrentStoryComments());
    }

    public void onViewRestored(StoryDetailView detailView) {
        view = detailView;
        view.showStory(readingSession.getCurrentStory());
        view.showComments(readingSession.getCurrentStoryComments());
    }

    public void onViewResumed(StoryDetailView detailView) {

    }

    public void onViewPaused(StoryDetailView detailView) {
        view = null;
    }

    public void onReadButtonClicked() {
        if(view != null){
            view.showStoryPostUrl(readingSession.getCurrentStory().getUrl());
        }
    }
}
