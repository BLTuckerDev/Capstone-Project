package dev.bltucker.nanodegreecapstone.common.injection;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dev.bltucker.nanodegreecapstone.common.ApplicationViewModelsFactory;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailViewModel;

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelMapKey(StoryDetailViewModel.class)
    abstract ViewModel bindStoryDetailViewModel(StoryDetailViewModel storyDetailViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ApplicationViewModelsFactory applicationViewModelsFactory);
}
