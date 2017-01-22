package dev.bltucker.nanodegreecapstone.topstories;

import android.accounts.Account;

import com.google.android.gms.analytics.Tracker;

import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.injection.StoryMax;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;

@Module
@ApplicationScope
public class TopStoriesModule {

    @Provides
    @ApplicationScope
    public SyncRequestDelegate provideSyncRequestDelegate(Account account){
        return new SyncRequestDelegate(account);
    }

    @Provides
    @ApplicationScope
    public StoryListViewPresenter provideStoryListViewPresenter(ReadingSession readingSession,
                                                                StoryListLoaderCallbackDelegate storyListLoaderCallbackDelegate,
                                                                SyncRequestDelegate syncRequestDelegate){
        return new StoryListViewPresenter(readingSession, storyListLoaderCallbackDelegate, syncRequestDelegate);
    }


    @Provides
    @ApplicationScope
    public StoryListAdapter provideStoryListAdapter(StoryListViewPresenter presenter, @StoryMax int storyMax, ReadingSession readingSession){
        return new StoryListAdapter(presenter, storyMax, readingSession);
    }


    @Provides
    @ApplicationScope
    public StoryListLoaderCallbackDelegate provideStoryListLoaderCallbackDelegate(ReadingSession readingSession, Provider<StoryListLoader> storyListLoaderProvider){
        return new StoryListLoaderCallbackDelegate(readingSession, storyListLoaderProvider);
    }

}
