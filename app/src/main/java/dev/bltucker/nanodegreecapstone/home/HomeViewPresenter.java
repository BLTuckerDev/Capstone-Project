package dev.bltucker.nanodegreecapstone.home;

import android.support.annotation.VisibleForTesting;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope;

@ApplicationScope
public class HomeViewPresenter {

    @VisibleForTesting
    HomeView view;

    @Inject
    public HomeViewPresenter(){
    }

    public void onViewCreated(HomeView createdView){
        view = createdView;
    }

    public void onViewRestored(HomeView restoredView){
        view = restoredView;
    }

    public void onViewDestroyed(){
        view = null;
    }

    public void onShowReadLaterMenuClick() {
        if(view != null){
            view.showReadLaterListView();
        }
    }
}
