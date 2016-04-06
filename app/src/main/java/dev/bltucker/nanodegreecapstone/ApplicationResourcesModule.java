package dev.bltucker.nanodegreecapstone;

import android.content.Context;

import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.data.CombinationBackedStoryRepository;
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@ApplicationScope
public class ApplicationResourcesModule {

    private final CapstoneApplication application;

    public ApplicationResourcesModule(CapstoneApplication application){
        this.application = application;
    }

    @Provides
    @ApplicationScope
    public Gson provideGson(){
        return new Gson();
    }


    @Provides
    @ApplicationScope
    public Context provideApplicationContext(){
        return application;
    }

    @Provides
    @ApplicationScope
    public Retrofit provideRetrofitClient(Gson gson){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hacker-news.firebaseio.com/v0/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit;
    }

    @Provides
    @ApplicationScope
    public HackerNewsApiService provideApiService(Retrofit retrofit){
        return retrofit.create(HackerNewsApiService.class);
    }

    @Provides
    @ApplicationScope
    public StoryRepository provideStoryRepository(CombinationBackedStoryRepository repository){
        return repository;
    }

    @Provides
    @ApplicationScope
    public StoryListFragmentPresenter provideStoryListFragmentPresenter(StoryRepository repo, ReadingSession readingSession){
        return new StoryListFragmentPresenter(repo, readingSession);
    }

    @Provides
    @ApplicationScope
    public ReadingSession provideReadingSession(){
        return new ReadingSession();
    }

    @Provides
    @ApplicationScope
    public StoryListAdapter provideStoryListAdapter(StoryListFragmentPresenter presenter){
        return new StoryListAdapter(presenter);
    }
}
