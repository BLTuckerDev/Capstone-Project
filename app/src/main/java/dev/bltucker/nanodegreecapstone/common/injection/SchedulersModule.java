package dev.bltucker.nanodegreecapstone.common.injection;

import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.IO;
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.UI;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Module
public class SchedulersModule {

    @Provides
    @ApplicationScope
    @NonNull
    @UI
    public Scheduler provideUiScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @ApplicationScope
    @NonNull
    @IO
    public Scheduler provideIoScheduler() {
        return Schedulers.io();
    }

}
