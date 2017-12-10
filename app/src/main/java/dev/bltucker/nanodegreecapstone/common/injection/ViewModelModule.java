package dev.bltucker.nanodegreecapstone.common.injection;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dev.bltucker.nanodegreecapstone.common.ApplicationViewModelsFactory;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailViewModel;
import dev.bltucker.nanodegreecapstone.topstories.TopStoriesViewModel;

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelMapKey(StoryDetailViewModel.class)
    abstract ViewModel bindStoryDetailViewModel(StoryDetailViewModel storyDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelMapKey(TopStoriesViewModel.class)
    abstract ViewModel bindTopStoryViewModel(TopStoriesViewModel topStoriesViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ApplicationViewModelsFactory applicationViewModelsFactory);
}
