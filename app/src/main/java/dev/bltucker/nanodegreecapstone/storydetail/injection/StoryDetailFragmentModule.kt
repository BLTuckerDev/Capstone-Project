package dev.bltucker.nanodegreecapstone.storydetail.injection

import android.arch.lifecycle.LifecycleRegistry
import android.content.res.Resources
import android.os.Bundle
import android.support.annotation.Nullable
import dagger.Module
import dagger.Provides
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.GregorianUTC
import dev.bltucker.nanodegreecapstone.common.models.Story
import dev.bltucker.nanodegreecapstone.storydetail.StoryCommentsAdapter
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailFragment
import java.util.*

@Module
class StoryDetailFragmentModule(private val fragment: StoryDetailFragment,
                                private val savedInstanceState: Bundle?) {

    @Provides
    @StoryDetailFragmentScope
    fun provideLifecycleRegistry(): LifecycleRegistry {
        return LifecycleRegistry(fragment)
    }

    @Provides
    @StoryDetailFragmentScope
    @Nullable
    fun provideStory(): Story? {
        return fragment.arguments.getParcelable<Story?>(STORY_BUNDLE_KEY)
    }

    @Provides
    @StoryDetailFragmentScope
    fun provideStoryCommentsAdapter(@GregorianUTC calendar: Calendar, resources: Resources): StoryCommentsAdapter {
        return StoryCommentsAdapter(calendar, resources)
    }

    companion object {

        val STORY_BUNDLE_KEY = "story"

        val DETAIL_STORY_BUNDLE_KEY = "detailStory"
    }
}
