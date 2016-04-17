package dev.bltucker.nanodegreecapstone.topstories;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.data.StoryCommentsLoader;
import dev.bltucker.nanodegreecapstone.data.StoryListLoader;
import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.home.HomeViewPresenter;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.injection.StoryMax;
import dev.bltucker.nanodegreecapstone.injection.SyncIntervalSeconds;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;

@Module
public class TopStoriesModule {

    @Provides
    @ApplicationScope
    public StoryListViewPresenter provideStoryListViewPresenter(StoryRepository repo, ReadingSession readingSession, EventBus eventBus, StoryListLoader storyListLoader, StoryCommentsLoader commentsLoader){
        return new StoryListViewPresenter(repo, readingSession, eventBus, storyListLoader, commentsLoader);
    }


    @Provides
    @ApplicationScope
    public StoryListAdapter provideStoryListAdapter(StoryListViewPresenter presenter, @StoryMax int storyMax){
        return new StoryListAdapter(presenter, storyMax);
    }

    @Provides
    @ApplicationScope
    public HomeViewPresenter provideHomeViewPresenter(@SyncIntervalSeconds int syncInterval){
        return new HomeViewPresenter(syncInterval);
    }

}
