package dev.bltucker.nanodegreecapstone.storydetail.injection;

import android.arch.lifecycle.LifecycleRegistry;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.injection.GregorianUTC;
import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.storydetail.StoryCommentsAdapter;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailFragment;

@Module
public class StoryDetailFragmentModule {

    public static final String STORY_BUNDLE_KEY = "story";

    public static final String DETAIL_STORY_BUNDLE_KEY = "detailStory";

    private final StoryDetailFragment fragment;

    @Nullable
    private final Bundle savedInstanceState;

    public StoryDetailFragmentModule(@NonNull StoryDetailFragment fragment, @Nullable Bundle savedInstanceState){
        this.fragment = fragment;
        this.savedInstanceState = savedInstanceState;
    }

    @Provides
    @StoryDetailFragmentScope
    public LifecycleRegistry provideLifecycleRegistry(){
        return new LifecycleRegistry(fragment);
    }

    @Provides
    @StoryDetailFragmentScope
    @Nullable
    public Story provideStory(){
        return (Story) fragment.getArguments().getParcelable(STORY_BUNDLE_KEY);
    }

    @Provides
    @StoryDetailFragmentScope
    public StoryCommentsAdapter provideStoryCommentsAdapter(@GregorianUTC Calendar calendar, Resources resources){
        return new StoryCommentsAdapter(calendar, resources);
    }
}
