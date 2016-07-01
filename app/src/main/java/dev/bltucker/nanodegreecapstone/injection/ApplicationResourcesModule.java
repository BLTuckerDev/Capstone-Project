package dev.bltucker.nanodegreecapstone.injection;

import android.accounts.Account;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.TimeZone;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.data.CommentRepository;
import dev.bltucker.nanodegreecapstone.data.ContentProviderBackedStoryRepository;
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.storydetail.CommentTypeAdapter;
import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter;
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
    public ContentResolver provideContentResolver(){
        return application.getContentResolver();
    }

    @Provides
    @ApplicationScope
    public NotificationManager provideNotificationManager(){
        return (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    @ApplicationScope
    public Tracker provideTracker(){
        return GoogleAnalytics.getInstance(application).newTracker(R.xml.global_tracker);
    }

    @Provides
    @ApplicationScope
    public Resources provideResources(Context context){
        return context.getResources();
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
    public Account provideAccount(){
        return new Account(StorySyncAdapter.ACCOUNT, StorySyncAdapter.ACCOUNT_TYPE);
    }

    @Provides
    @ApplicationScope
    @GregorianUTC
    public Calendar provideCalendar(){
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }

    @Provides
    @ApplicationScope
    public Gson provideGson(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Comment.class, new CommentTypeAdapter());
        return builder.create();
    }


    @Provides
    @ApplicationScope
    public Context provideApplicationContext(){
        return application;
    }

    @Provides
    @ApplicationScope
    public Retrofit provideRetrofitClient(Gson gson){
        return new Retrofit.Builder()
                .baseUrl("https://hacker-news.firebaseio.com/v0/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @ApplicationScope
    public HackerNewsApiService provideApiService(Retrofit retrofit){
        return retrofit.create(HackerNewsApiService.class);
    }

    @Provides
    @ApplicationScope
    public StoryRepository provideStoryRepository(ContentResolver contentResolver, CommentRepository commentRepository){
        return new ContentProviderBackedStoryRepository(contentResolver, commentRepository);
    }

    @Provides
    @ApplicationScope
    public CommentRepository provideCommentRepository(HackerNewsApiService hackerNewsApiService, ContentResolver contentResolver){
        return new CommentRepository(hackerNewsApiService, contentResolver);
    }

    @Provides
    @ApplicationScope
    public EventBus provideEventBus(){
        return new EventBus();
    }



    @Provides
    @ApplicationScope
    public ReadingSession provideReadingSession(@StoryMax int storyMax, EventBus eventBus){
        return new ReadingSession(storyMax, eventBus);
    }
}
