package dev.bltucker.nanodegreecapstone.common.events;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope;

@ApplicationScope
public class SyncStatusObserver {

    private volatile boolean syncInProgress = false;

    @SuppressWarnings("squid:S1186")
    @Inject
    public SyncStatusObserver() {
    }

    public boolean isSyncInProgress() {
        return syncInProgress;
    }

    public void setSyncInProgress(boolean status) {
        syncInProgress = status;
    }

}
