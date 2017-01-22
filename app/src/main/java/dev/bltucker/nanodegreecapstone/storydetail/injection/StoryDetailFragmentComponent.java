package dev.bltucker.nanodegreecapstone.storydetail.injection;

import dagger.Subcomponent;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailFragment;

@Subcomponent(modules = {StoryDetailFragmentModule.class})
@StoryDetailFragmentScope
public interface StoryDetailFragmentComponent {
    void inject(StoryDetailFragment fragment);
}
