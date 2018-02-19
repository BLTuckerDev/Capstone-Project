package dev.bltucker.nanodegreecapstone.readlater;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.IO;
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.UI;
import dev.bltucker.nanodegreecapstone.common.data.StoryProvider;
import dev.bltucker.nanodegreecapstone.common.models.ReadLaterStory;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;

@ReadLaterListFragmentScope
public class ReadLaterListPresenter implements LoaderManager.LoaderCallbacks<List<ReadLaterStory>>, SwipeRefreshLayout.OnRefreshListener {

    private final ContentResolver contentResolver;
    private final android.support.v4.app.LoaderManager loaderManager;
    private final Provider<ReadLaterStoryListLoader> readLaterStoryListLoaderProvider;

    @NonNull
    private final Scheduler uiScheduler;

    @NonNull
    private final Scheduler ioScheduler;

    private ReadLaterListView view;

    private boolean initializeLoaderOnRestore = false;

    @Inject
    public ReadLaterListPresenter(ContentResolver contentResolver,
                                  android.support.v4.app.LoaderManager loaderManager,
                                  Provider<ReadLaterStoryListLoader> readLaterStoryListLoaderProvider,
                                  @NonNull @UI Scheduler uiScheduler,
                                  @NonNull @IO Scheduler ioScheduler) {
        this.contentResolver = contentResolver;
        this.loaderManager = loaderManager;
        this.readLaterStoryListLoaderProvider = readLaterStoryListLoaderProvider;
        this.uiScheduler = uiScheduler;
        this.ioScheduler = ioScheduler;
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

    public void onReadLaterStoryClicked(ReadLaterStory story) {
        if (view != null) {
            view.readStory(story);
        }
    }

    public void onReadLaterStoryDeleteClicked(final ReadLaterStory story) {
        Completable.fromAction(() -> contentResolver.delete(Uri.withAppendedPath(StoryProvider.READ_LATER_URI, String.valueOf(story.getId())), null, null))
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        loaderManager.restartLoader(ReadLaterStoryListLoader.ID, null, ReadLaterListPresenter.this);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }
}
