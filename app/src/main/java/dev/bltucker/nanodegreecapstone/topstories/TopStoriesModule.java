package dev.bltucker.nanodegreecapstone.topstories;

import android.accounts.Account;

import javax.inject.Provider;

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
    public StoryListViewPresenter provideStoryListViewPresenter(ReadingSession readingSession, EventBus eventBus,
                                                                StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate, StoryCommentLoaderCallbackDelegate storyCommentLoaderCallbackDelegate,
                                                                Account account){
        return new StoryListViewPresenter(readingSession, eventBus, storyListLoaderCallbackDelegate, storyCommentLoaderCallbackDelegate, account);
    }


    @Provides
    @ApplicationScope
    public StoryListAdapter provideStoryListAdapter(StoryListViewPresenter presenter, @StoryMax int storyMax, ReadingSession readingSession){
        return new StoryListAdapter(presenter, storyMax, readingSession);
    }

    @Provides
    @ApplicationScope
    public HomeViewPresenter provideHomeViewPresenter(@SyncIntervalSeconds int syncInterval, Account account){
        return new HomeViewPresenter(syncInterval, account);
    }

    @Provides
    @ApplicationScope
    public StoryListLoaderCallbackDelegate provideStoryListLoaderCallbackDelegate(ReadingSession readingSession, Provider<StoryListLoader> storyListLoaderProvider){
        return new StoryListLoaderCallbackDelegate(readingSession, storyListLoaderProvider);
    }

    @Provides
    @ApplicationScope
    public StoryCommentLoaderCallbackDelegate provideStoryCommentLoaderCallbackDelegate(ReadingSession readingSession, Provider<StoryCommentsLoader> storyCommentsLoaderProvider){
        return new StoryCommentLoaderCallbackDelegate(readingSession, storyCommentsLoaderProvider);
    }

}
