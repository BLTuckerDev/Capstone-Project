package dev.bltucker.nanodegreecapstone.injection;

import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.MockHackerNewsApiService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@ApplicationScope
public class TestApplicationResourcesModule extends ApplicationResourcesModule {

    public TestApplicationResourcesModule(CapstoneApplication application) {
        super(application);
    }

    @Override
    @Provides
    @ApplicationScope
    public Retrofit provideRetrofitClient(Gson gson) {
        return new Retrofit.Builder()
                .baseUrl("https://hacker-news.firebaseio.com/v0/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @ApplicationScope
    @Override
    public HackerNewsApiService provideApiService(Retrofit retrofit) {
        return new MockHackerNewsApiService(provideGson());
    }
}
