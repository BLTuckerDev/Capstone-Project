package dev.bltucker.nanodegreecapstone.common.injection;

import com.google.gson.Gson;

import android.arch.persistence.room.Room;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.IO;
import dev.bltucker.nanodegreecapstone.common.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.common.data.HackerNewsDatabase;
import dev.bltucker.nanodegreecapstone.common.data.MockHackerNewsApiService;
import dev.bltucker.nanodegreecapstone.common.data.migrations.Version1to2;
import dev.bltucker.nanodegreecapstone.common.data.migrations.Version2to3;
import io.reactivex.Scheduler;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class TestApplicationResourcesModule extends ApplicationResourcesModule {

    public TestApplicationResourcesModule(CapstoneApplication application) {
        super(application);
    }

    @Provides
    @ApplicationScope
    HackerNewsDatabase providesHackerNewsDatabase() {
        return Room.inMemoryDatabaseBuilder(application, HackerNewsDatabase.class)
                .allowMainThreadQueries()
                .addMigrations(new Version1to2(1, 2))
                .addMigrations(new Version2to3(2, 3))
                .build();
    }

    @Override
    @Provides
    @ApplicationScope
    public Retrofit provideRetrofitClient(Gson gson, OkHttpClient okHttpClient, @IO Scheduler ioScheduler) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://hacker-news.firebaseio.com/v0/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(ioScheduler))
                .build();
    }

    @Provides
    @ApplicationScope
    @Override
    public HackerNewsApiService provideApiService(Retrofit retrofit) {
        return new MockHackerNewsApiService(provideGson());
    }
}
