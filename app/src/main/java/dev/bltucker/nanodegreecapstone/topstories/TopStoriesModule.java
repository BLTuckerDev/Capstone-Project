package dev.bltucker.nanodegreecapstone.topstories;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.data.StoryCommentsLoader;
import dev.bltucker.nanodegreecapstone.data.StoryListLoader;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.home.HomeViewPresenter;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.injection.StoryMax;
import dev.bltucker.nanodegreecapstone.injection.SyncIntervalSeconds;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;

@Module
@ApplicationScope
public class TopStoriesModule {

    @Provides
    @ApplicationScope
    public StoryListViewPresenter provideStoryListViewPresenter(ReadingSession readingSession, EventBus eventBus, StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate, StoryCommentLoaderCallbackDelegate storyCommentLoaderCallbackDelegate){
        return new StoryListViewPresenter(readingSession, eventBus, storyListLoaderCallbackDelegate, storyCommentLoaderCallbackDelegate);
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

    @Provides
    @ApplicationScope
    public StoryListLoaderCallbackDelegate provideStoryListLoaderCallbackDelegate(ReadingSession readingSession, StoryListLoader loader){
        return new StoryListLoaderCallbackDelegate(readingSession, loader);
    }

    @Provides
    @ApplicationScope
    public StoryCommentLoaderCallbackDelegate provideStoryCommentLoaderCallbackDelegate(ReadingSession readingSession, StoryCommentsLoader loader){
        return new StoryCommentLoaderCallbackDelegate(readingSession, loader);
    }

}
