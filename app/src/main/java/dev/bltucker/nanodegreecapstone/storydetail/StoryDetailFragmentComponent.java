package dev.bltucker.nanodegreecapstone.storydetail;

import dagger.Subcomponent;

@Subcomponent(modules = {StoryDetailFragmentModule.class})
@StoryDetailFragmentScope
public interface StoryDetailFragmentComponent {
    void inject(StoryDetailFragment fragment);
}
