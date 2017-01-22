package dev.bltucker.nanodegreecapstone.injection;

import android.accounts.Account;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.util.LruCache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.data.CommentRepository;
import dev.bltucker.nanodegreecapstone.data.ContentProviderBackedStoryRepository;
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.StoryDatabase;
import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;
import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@ApplicationScope
public class ApplicationResourcesModule {

    private final CapstoneApplication application;

    public ApplicationResourcesModule(CapstoneApplication application) {
        this.application = application;
    }

    @Provides
    @ApplicationScope
    public StoryDatabase provideStoryDatabase(){
        return StoryDatabase.getInstance(application);
    }

    @Provides
    @ApplicationScope
    public ContentResolver provideContentResolver() {
        return application.getContentResolver();
    }

    @Provides
    @ApplicationScope
    public NotificationManager provideNotificationManager() {
        return (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    @ApplicationScope
    public Resources provideResources(Context context) {
        return context.getResources();
    }

    @Provides
    @ApplicationScope
    @StoryMax
    public int provideMaxStoryInt(Resources resources) {
        return resources.getInteger(R.integer.reading_session_story_max);
    }

    @Provides
    @ApplicationScope
    @SyncIntervalSeconds
    public int provideSyncIntervalInSeconds(Resources resources) {
        return resources.getInteger(R.integer.sync_interval_seconds);
    }

    @Provides
    @ApplicationScope
    @CommentCacheSize
    public int provideCommentCacheSize(Resources resources){
        return resources.getInteger(R.integer.comment_cache_size);
    }

    @Provides
    @ApplicationScope
    public Account provideAccount() {
        return new Account(StorySyncAdapter.ACCOUNT, StorySyncAdapter.ACCOUNT_TYPE);
    }

    @Provides
    @ApplicationScope
    @GregorianUTC
    public Calendar provideCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }

    @Provides
    @ApplicationScope
    @CommentListCache
    public LruCache<Long, List<Comment>> provideLruCache(@CommentCacheSize int cacheSize){
        return new LruCache<>(cacheSize);
    }

    @Provides
    @ApplicationScope
    public Gson provideGson() {
        GsonBuilder builder = new GsonBuilder();
        return builder.create();
    }

    @Provides
    @ApplicationScope
    public Context provideApplicationContext() {
        return application;
    }

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
    public HackerNewsApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(HackerNewsApiService.class);
    }

    @Provides
    @ApplicationScope
    public StoryRepository provideStoryRepository(ContentResolver contentResolver, CommentRepository commentRepository) {
        return new ContentProviderBackedStoryRepository(contentResolver, commentRepository);
    }

    @Provides
    @ApplicationScope
    public EventBus provideEventBus() {
        return new EventBus();
    }

    @Provides
    @ApplicationScope
    public ReadingSession provideReadingSession(@StoryMax int storyMax, EventBus eventBus) {
        return new ReadingSession(storyMax, eventBus);
    }
}
