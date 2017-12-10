package dev.bltucker.nanodegreecapstone.topstories;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.common.ApplicationViewModelsFactory;
import dev.bltucker.nanodegreecapstone.common.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.UI;
import dev.bltucker.nanodegreecapstone.databinding.FragmentStoryListBinding;
import dev.bltucker.nanodegreecapstone.events.SyncStatusObserver;
import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.topstories.events.TopStoryClickEvent;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class TopStoriesFragment extends Fragment {

    @Inject
    SyncStatusObserver syncStatusObserver;

    @Inject
    StoryListAdapter adapter;

    @Inject
    ApplicationViewModelsFactory applicationViewModelsFactory;

    @Inject
    @UI
    Scheduler uiScheduler;

    private CompositeDisposable clickEventDisposable = new CompositeDisposable();

    @Nullable
    private Disposable refreshEventDispoable;

    @Nullable
    private Disposable modelDisposable;

    private TopStoriesViewModel topStoriesViewModel;

    private Delegate delegate;

    private FragmentStoryListBinding binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Delegate) {
            this.delegate = (Delegate) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerInjector.getApplicationComponent().inject(this);
        topStoriesViewModel = ViewModelProviders.of(this, applicationViewModelsFactory).get(TopStoriesViewModel.class);
        topStoriesViewModel.onRefreshTopStories();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStoryListBinding.inflate(inflater, container, false);
        binding.swipeToRefreshLayout.setOnRefreshListener(() -> topStoriesViewModel.onRefreshTopStories());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.storyListRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.storyListRecyclerview.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        topStoriesViewModel.getObservableRefreshEvents()
                .observeOn(uiScheduler)
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        refreshEventDispoable = d;
                    }

                    @Override
                    public void onNext(Object o) {
                        binding.swipeToRefreshLayout.setRefreshing(false);
                        showUpdatedStoriesNotification();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        topStoriesViewModel.getObservableModelEvents()
                .observeOn(uiScheduler)
                .subscribe(new Observer<TopStoryModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        modelDisposable = d;
                    }

                    @Override
                    public void onNext(TopStoryModel topStoryModel) {
                        adapter.updateStories(topStoryModel.getStoryList());
                       binding.setTopStoryModel(topStoryModel);
                       binding.executePendingBindings();
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onComplete() {}
                });


    }

    @Override
    public void onResume() {
        super.onResume();
        topStoriesViewModel.getObservableClickEvents()
                .observeOn(uiScheduler)
                .subscribe(new Observer<TopStoryClickEvent>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        clickEventDisposable.add(d);
                    }

                    @Override
                    public void onNext(TopStoryClickEvent topStoryClickEvent) {
                        topStoryClickEvent.execute(TopStoriesFragment.this);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void onPause() {
        clickEventDisposable.clear();
        super.onPause();
    }

    @Override
    public void onStop() {
        if (refreshEventDispoable != null) {
            refreshEventDispoable.dispose();
        }

        if(modelDisposable != null){
            modelDisposable.dispose();
        }
        super.onStop();
    }

    @Override
    public void onDetach() {
        this.delegate = null;
        super.onDetach();
    }

    public void showStoryDetailView(Story story) {
        if (delegate != null) {
            delegate.showCommentsView(story);
        }
    }

    public void showStoryPostUrl(String url) {
        FragmentActivity activity = this.getActivity();

        if (null == activity) {
            return;
        }

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ResourcesCompat.getColor(activity.getResources(), R.color.colorPrimary, activity.getTheme()));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(activity, Uri.parse(url));
    }

    public void showUpdatedStoriesNotification() {
        if (null == getView()) {
            return;
        }

        //TODO need tohide spinner?
        binding.swipeToRefreshLayout.setRefreshing(false);

        Snackbar.make(getView(), R.string.new_stories_are_available, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.refresh, v -> topStoriesViewModel.onShowRefreshedTopStories())
                .show();
    }

    public interface Delegate {
        void showCommentsView(Story story);
    }
}
