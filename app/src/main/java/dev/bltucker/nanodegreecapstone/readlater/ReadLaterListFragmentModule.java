package dev.bltucker.nanodegreecapstone.readlater;

import android.content.ContentResolver;
import android.support.v4.app.LoaderManager;

import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;

@Module
public class ReadLaterListFragmentModule {

    private final ReadLaterListFragment fragment;

    public ReadLaterListFragmentModule(ReadLaterListFragment fragment){
        this.fragment = fragment;
    }

    @Provides
    @ReadLaterListFragmentScope
    public LoaderManager provideLoaderManager(){
        return fragment.getLoaderManager();
    }

    @Provides
    @ReadLaterListFragmentScope
    public ReadLaterListPresenter providePresenter(ContentResolver contentResolver, LoaderManager loaderManager, Provider<ReadLaterStoryListLoader> loaderProvider){
        return new ReadLaterListPresenter(contentResolver, loaderManager, loaderProvider);
    }

    @Provides
    @ReadLaterListFragmentScope
    public ReadLaterRepository provideReadLaterRepository(ContentProviderBackedRepository repository){
        return repository;
    }

}
