package dev.bltucker.nanodegreecapstone.storydetail.injection;

import android.os.HandlerThread;
import android.os.Looper;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.storydetail.data.InterruptibleDownloadService;
import dev.bltucker.nanodegreecapstone.storydetail.data.ServiceMessageHandler;

@Module
public class InterruptibleDownloadServiceModule {

    private InterruptibleDownloadService service;

    public InterruptibleDownloadServiceModule(InterruptibleDownloadService service){
        this.service = service;
    }

    @Provides
    @InterruptibleDownloadServiceScope
    public HandlerThread provideHandlerThread(){
        HandlerThread handlerThread = new HandlerThread(InterruptibleDownloadService.class.getName());
        handlerThread.start();
        return handlerThread;
    }

    @Provides
    @InterruptibleDownloadServiceScope
    public ServiceMessageHandler provideServiceMessageHandler(HandlerThread handlerThread){
        return new ServiceMessageHandler(service, handlerThread.getLooper());
    }
}
