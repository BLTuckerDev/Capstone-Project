package dev.bltucker.nanodegreecapstone.readlater;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import dev.bltucker.nanodegreecapstone.models.ReadLaterStory;

public class ReadLaterListPresenter implements LoaderManager.LoaderCallbacks<List<ReadLaterStory>>, SwipeRefreshLayout.OnRefreshListener {

    private final android.support.v4.app.LoaderManager loaderManager;
    private final Provider<ReadLaterStoryListLoader> readLaterStoryListLoaderProvider;

    private ReadLaterListView view;

    private boolean initializeLoaderOnRestore = false;

    @Inject
    public ReadLaterListPresenter(android.support.v4.app.LoaderManager loaderManager, Provider<ReadLaterStoryListLoader> readLaterStoryListLoaderProvider) {
        this.loaderManager = loaderManager;
        this.readLaterStoryListLoaderProvider = readLaterStoryListLoaderProvider;
    }

    public void onViewCreated(ReadLaterListView listView) {
        view = listView;
        view.showLoadingSpinner();
        initializeLoader();
    }

    public void onViewRestored(ReadLaterListView listView) {
        view = listView;
        if (initializeLoaderOnRestore) {
            initializeLoaderOnRestore = false;
            initializeLoader();
        }
    }

    private void initializeLoader() {
        loaderManager.initLoader(ReadLaterStoryListLoader.ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<List<ReadLaterStory>> onCreateLoader(int id, Bundle args) {
        return readLaterStoryListLoaderProvider.get();
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<ReadLaterStory>> loader, List<ReadLaterStory> data) {
        if (view != null) {
            view.hideLoadingSpinner();
            if (data.isEmpty()) {
                view.showEmptyView();
            } else {
                view.showStories(data);
            }
        } else {
            initializeLoaderOnRestore = true;
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<ReadLaterStory>> loader) {
        if (view != null) {
            view.showEmptyView();
        }
    }

    @Override
    public void onRefresh() {
        loaderManager.restartLoader(ReadLaterStoryListLoader.ID, null, this);
    }
}
