package dev.bltucker.nanodegreecapstone.injection;

import com.google.gson.Gson;

import android.arch.persistence.room.Room;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.HackerNewsDatabase;
import dev.bltucker.nanodegreecapstone.data.MockHackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.migrations.Version1to2;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@ApplicationScope
public class TestApplicationResourcesModule extends ApplicationResourcesModule {

    public TestApplicationResourcesModule(CapstoneApplication application) {
        super(application);
    }

    @Provides
    @ApplicationScope
    HackerNewsDatabase providesHackerNewsDatabase(){
        return Room.inMemoryDatabaseBuilder(application, HackerNewsDatabase.class)
                .addMigrations(new Version1to2(1, 2))
                .build();
    }

    @Override
    @Provides
    @ApplicationScope
    public Retrofit provideRetrofitClient(Gson gson) {
        return new Retrofit.Builder()
                .baseUrl("https://hacker-news.firebaseio.com/v0/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @ApplicationScope
    @Override
    public HackerNewsApiService provideApiService(Retrofit retrofit) {
        return new MockHackerNewsApiService(provideGson());
    }
}
