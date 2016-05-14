package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.Context;

import com.google.android.gms.analytics.Tracker;

import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.data.StoryCommentsLoader;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;

@Module
@ApplicationScope
public class StoryDetailModule {

    @Provides
    @ApplicationScope
    public StoryDetailViewPresenter provideStoryDetailPresenter(Context context,
                                                                ReadingSession readingSession,
                                                                Tracker tracker,
                                                                StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate){
        return new StoryDetailViewPresenter(context, readingSession, tracker, commentLoaderCallbackDelegate);
    }

    @Provides
    @ApplicationScope
    public StoryCommentLoaderCallbackDelegate provideStoryCommentLoaderCallbackDelegate(ReadingSession readingSession, Provider<StoryCommentsLoader> storyCommentsLoaderProvider){
        return new StoryCommentLoaderCallbackDelegate(readingSession, storyCommentsLoaderProvider);
    }
}
