package dev.bltucker.nanodegreecapstone.storydetail;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.analytics.Tracker;

import java.util.Calendar;

import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.data.StoryCommentsLoader;
import dev.bltucker.nanodegreecapstone.injection.GregorianUTC;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;

@Module
public class StoryDetailFragmentModule {

    private final StoryDetailFragment fragment;

    public StoryDetailFragmentModule(StoryDetailFragment fragment){
        this.fragment = fragment;
    }

    @Provides
    @StoryDetailFragmentScope
    public LoaderManager provideLoaderManager(){
        return fragment.getLoaderManager();
    }

    @Provides
    @StoryDetailFragmentScope
    public StoryDetailViewPresenter provideStoryDetailPresenter(ContentResolver contentResolver,
                                                                ReadingSession readingSession,
                                                                Tracker tracker,
                                                                StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate){
        return new StoryDetailViewPresenter(contentResolver, readingSession, tracker, commentLoaderCallbackDelegate);
    }

    @Provides
    @StoryDetailFragmentScope
    public StoryCommentLoaderCallbackDelegate provideStoryCommentLoaderCallbackDelegate(ReadingSession readingSession, Provider<StoryCommentsLoader> storyCommentsLoaderProvider){
        return new StoryCommentLoaderCallbackDelegate(readingSession, storyCommentsLoaderProvider);
    }

    @Provides
    @StoryDetailFragmentScope
    public StoryCommentsAdapter provideStoryCommentsAdapter(@GregorianUTC Calendar calendar, Resources resources, ReadingSession readingSession){
        return new StoryCommentsAdapter(calendar, resources, readingSession);
    }
}
