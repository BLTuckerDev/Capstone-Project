package dev.bltucker.nanodegreecapstone.storydetail.injection

import dagger.Subcomponent
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailFragment

@Subcomponent(modules = arrayOf(StoryDetailFragmentModule::class))
@StoryDetailFragmentScope
interface StoryDetailFragmentComponent {
    fun inject(fragment: StoryDetailFragment)
}
