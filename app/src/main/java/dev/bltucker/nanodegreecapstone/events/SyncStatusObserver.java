package dev.bltucker.nanodegreecapstone.events;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;

@ApplicationScope
public class SyncStatusObserver {

    private volatile boolean syncInProgress = false;

    @Inject
    public SyncStatusObserver(){
    }

    public boolean isSyncInProgress() {
        return syncInProgress;
    }

    public void setSyncInProgress(boolean status){
        syncInProgress = status;
    }

}
