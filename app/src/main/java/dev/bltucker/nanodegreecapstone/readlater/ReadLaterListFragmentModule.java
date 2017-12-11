package dev.bltucker.nanodegreecapstone.readlater;

import android.support.v4.app.LoaderManager;

import dagger.Module;
import dagger.Provides;

@Module
public class ReadLaterListFragmentModule {

    private final ReadLaterListFragment fragment;

    public ReadLaterListFragmentModule(ReadLaterListFragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    @ReadLaterListFragmentScope
    public LoaderManager provideLoaderManager() {
        return fragment.getLoaderManager();
    }

    @Provides
    @ReadLaterListFragmentScope
    public ReadLaterRepository provideReadLaterRepository(ContentProviderBackedRepository repository) {
        return repository;
    }

}
