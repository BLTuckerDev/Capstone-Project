package dev.bltucker.nanodegreecapstone.events;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import rx.Subscriber;
import rx.schedulers.Schedulers;

@ApplicationScope
public class SyncStatusObserver {

    private volatile boolean syncInProgress = false;

    @Inject
    public SyncStatusObserver(EventBus eventBus){

//        eventBus.subscribeTo(SyncStartedEvent.class)
//                .subscribeOn(Schedulers.computation())
//                .observeOn(Schedulers.computation())
//                .subscribe(new Subscriber<Object>() {
//                    @Override
//                    public void onCompleted() {}
//
//                    @Override
//                    public void onError(Throwable e) {
//                        syncInProgress = false;
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//                        syncInProgress = true;
//                    }
//                });
//
//        eventBus.subscribeTo(SyncCompletedEvent.class)
//                .subscribeOn(Schedulers.computation())
//                .observeOn(Schedulers.computation())
//                .subscribe(new Subscriber<Object>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        syncInProgress = false;
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//                        syncInProgress = false;
//                    }
//                });

    }

    public boolean isSyncInProgress() {
        return syncInProgress;
    }

    public void setSyncInProgress(boolean status){
        syncInProgress = status;
    }

}
