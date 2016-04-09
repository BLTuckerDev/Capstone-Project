package dev.bltucker.nanodegreecapstone.injection;

import android.content.Context;

import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.home.HomeViewPresenter;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.topstories.StoryListAdapter;
import dev.bltucker.nanodegreecapstone.topstories.StoryListViewPresenter;
import dev.bltucker.nanodegreecapstone.data.CombinationBackedStoryRepository;
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
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
    @StoryMax
    public int provideMaxStoryInt(Context context){
        return context.getResources().getInteger(R.integer.reading_session_story_max);
    }

    @Provides
    @ApplicationScope
    @SyncIntervalSeconds
    public int provideSyncIntervalInSeconds(Context context){
        return context.getResources().getInteger(R.integer.sync_interval_seconds);
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
    public StoryListAdapter provideStoryListAdapter(StoryListViewPresenter presenter, @StoryMax int storyMax){
        return new StoryListAdapter(presenter, storyMax);
    }

    @Provides
    @ApplicationScope
    public EventBus provideEventBus(){
        return new EventBus();
    }

    @Provides
    @ApplicationScope
    public HomeViewPresenter provideHomeViewPresenter(@SyncIntervalSeconds int syncInterval){
        return new HomeViewPresenter(syncInterval);
    }

    @Provides
    @ApplicationScope
    public ReadingSession provideReadingSession(@StoryMax int storyMax){
        return new ReadingSession(storyMax);
    }
}
