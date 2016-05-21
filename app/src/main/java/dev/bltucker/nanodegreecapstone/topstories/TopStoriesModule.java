package dev.bltucker.nanodegreecapstone.topstories;

import android.accounts.Account;

import com.google.android.gms.analytics.Tracker;

import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;
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
    public StoryListViewPresenter provideStoryListViewPresenter(ReadingSession readingSession,
                                                                StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate,
                                                                Account account,
                                                                Tracker tracker){
        return new StoryListViewPresenter(readingSession, storyListLoaderCallbackDelegate, account, tracker);
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

}
