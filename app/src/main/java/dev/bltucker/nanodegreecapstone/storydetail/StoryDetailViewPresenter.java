package dev.bltucker.nanodegreecapstone.storydetail;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;

public class StoryDetailViewPresenter {

    private final StoryRepository repository;
    private final ReadingSession readingSession;

    private StoryDetailView view;

    @Inject
    public StoryDetailViewPresenter(StoryRepository repository, ReadingSession readingSession){
        this.repository = repository;
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

    }
}
