package dev.bltucker.nanodegreecapstone.storydetail.injection;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.support.v4.app.LoaderManager;

import com.google.android.gms.analytics.Tracker;

import java.util.Calendar;

import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.injection.GregorianUTC;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.storydetail.DetailStoryProvider;
import dev.bltucker.nanodegreecapstone.storydetail.data.StoryCommentLoaderCallbackDelegate;
import dev.bltucker.nanodegreecapstone.storydetail.StoryCommentsAdapter;
import dev.bltucker.nanodegreecapstone.storydetail.data.StoryCommentsLoader;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailFragment;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailViewPresenter;

@Module
public class StoryDetailFragmentModule {

    private final StoryDetailFragment fragment;

    public StoryDetailFragmentModule(StoryDetailFragment fragment){
        this.fragment = fragment;
    }

    @Provides
    @StoryDetailFragmentScope
    public DetailStoryProvider provideDetailStoryProvider(){
        return new DetailStoryProvider();
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
                                                                StoryCommentLoaderCallbackDelegate commentLoaderCallbackDelegate,
                                                                LoaderManager loaderManager){
        return new StoryDetailViewPresenter(contentResolver, tracker, commentLoaderCallbackDelegate, loaderManager);
    }

    @Provides
    @StoryDetailFragmentScope
    public StoryCommentLoaderCallbackDelegate provideStoryCommentLoaderCallbackDelegate(ReadingSession readingSession, Provider<StoryCommentsLoader> storyCommentsLoaderProvider){
        return new StoryCommentLoaderCallbackDelegate(storyCommentsLoaderProvider);
    }

    @Provides
    @StoryDetailFragmentScope
    public StoryCommentsAdapter provideStoryCommentsAdapter(@GregorianUTC Calendar calendar, Resources resources){
        return new StoryCommentsAdapter(calendar, resources);
    }
}
