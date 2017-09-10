package dev.bltucker.nanodegreecapstone.storydetail.injection;

import android.content.res.Resources;

import java.util.Calendar;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.injection.GregorianUTC;
import dev.bltucker.nanodegreecapstone.storydetail.DetailStoryProvider;
import dev.bltucker.nanodegreecapstone.storydetail.StoryCommentsAdapter;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailFragment;

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
    public StoryCommentsAdapter provideStoryCommentsAdapter(@GregorianUTC Calendar calendar, Resources resources){
        return new StoryCommentsAdapter(calendar, resources);
    }
}
