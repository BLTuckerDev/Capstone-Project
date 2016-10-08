package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public final class ServiceMessageHandler extends Handler {

    private InterruptibleDownloadService interruptibleDownloadService;

    public ServiceMessageHandler(InterruptibleDownloadService interruptibleDownloadService, Looper looper) {
        super(looper);
        this.interruptibleDownloadService = interruptibleDownloadService;
    }

    @Override
    public void handleMessage(Message msg) {
        interruptibleDownloadService.processMessage(msg.getData());
        interruptibleDownloadService.stopSelf(msg.arg1);
    }
}
