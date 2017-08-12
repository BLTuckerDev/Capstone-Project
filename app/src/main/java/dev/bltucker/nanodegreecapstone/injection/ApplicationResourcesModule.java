package dev.bltucker.nanodegreecapstone.injection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.accounts.Account;
import android.app.NotificationManager;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.util.LruCache;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.StoryProvider;
import dev.bltucker.nanodegreecapstone.data.ContentProviderBackedStoryRepository;
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.HackerNewsDatabase;
import dev.bltucker.nanodegreecapstone.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.data.daos.CommentRefsDao;
import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.data.daos.ReadLaterStoryDao;
import dev.bltucker.nanodegreecapstone.data.daos.StoryDao;
import dev.bltucker.nanodegreecapstone.data.migrations.Version1to2;
import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static dev.bltucker.nanodegreecapstone.StoryProvider.ALL_COMMENTS_STORY_PATH_CODE;
import static dev.bltucker.nanodegreecapstone.StoryProvider.ALL_STORIES_PATH_CODE;
import static dev.bltucker.nanodegreecapstone.StoryProvider.COMMENTS_PATH_CODE;
import static dev.bltucker.nanodegreecapstone.StoryProvider.COMMENTS_WITH_PARENT_PATH_CODE;
import static dev.bltucker.nanodegreecapstone.StoryProvider.COMMENT_REFS_PATH_CODE;
import static dev.bltucker.nanodegreecapstone.StoryProvider.COMMENT_REFS_PATH_ITEM_CODE;
import static dev.bltucker.nanodegreecapstone.StoryProvider.READ_LATER_STORIES_PATH_CODE;
import static dev.bltucker.nanodegreecapstone.StoryProvider.READ_LATER_STORIES_PATH_ITEM_CODE;

@Module
@ApplicationScope
public class ApplicationResourcesModule {

    private final CapstoneApplication application;

    public ApplicationResourcesModule(CapstoneApplication application) {
        this.application = application;
    }

    @Provides
    @ApplicationScope
    HackerNewsDatabase providesHackerNewsDatabase(){
        return Room.databaseBuilder(application, HackerNewsDatabase.class, "databaseGenerator.db")
                .addMigrations(new Version1to2(1, 2))
                .build();
    }

    @Provides
    @ApplicationScope
    StoryDao provideStoryDao(HackerNewsDatabase hackerNewsDatabase){
        return hackerNewsDatabase.storyDao();
    }

    @Provides
    @ApplicationScope
    CommentRefsDao provideCommentRefsDao(HackerNewsDatabase hackerNewsDatabase){
        return hackerNewsDatabase.commentRefsDao();
    }

    @Provides
    @ApplicationScope
    ReadLaterStoryDao provideReadLaterStoryDao(HackerNewsDatabase hackerNewsDatabase){
        return hackerNewsDatabase.readLaterStoryDao();
    }

    @Provides
    @ApplicationScope
    CommentsDao provideCommentsDao(HackerNewsDatabase hackerNewsDatabase){
        return hackerNewsDatabase.commentsDao();
    }

    @Provides
    UriMatcher provideContentProviderUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(StoryProvider.AUTHORITY, StoryProvider.ALL_STORIES_PATH, ALL_STORIES_PATH_CODE);

        matcher.addURI(StoryProvider.AUTHORITY, StoryProvider.COMMENT_REFS_PATH, COMMENT_REFS_PATH_CODE);
        matcher.addURI(StoryProvider.AUTHORITY, StoryProvider.COMMENT_REFS_PATH + "/*", COMMENT_REFS_PATH_ITEM_CODE);

        matcher.addURI(StoryProvider.AUTHORITY, StoryProvider.COMMENTS_PATH, COMMENTS_PATH_CODE);
        matcher.addURI(StoryProvider.AUTHORITY, StoryProvider.COMMENTS_PATH + "/*", COMMENTS_WITH_PARENT_PATH_CODE);
        matcher.addURI(StoryProvider.AUTHORITY, StoryProvider.ALL_COMMENTS_FOR_STORY_PATH + "/*", ALL_COMMENTS_STORY_PATH_CODE);


        matcher.addURI(StoryProvider.AUTHORITY, StoryProvider.READ_LATER_STORY_PATH, READ_LATER_STORIES_PATH_CODE);
        matcher.addURI(StoryProvider.AUTHORITY, StoryProvider.READ_LATER_STORY_PATH + "/*", READ_LATER_STORIES_PATH_ITEM_CODE);

        return matcher;
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @ApplicationScope
    public HackerNewsApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(HackerNewsApiService.class);
    }

    @Provides
    @ApplicationScope
    public StoryRepository provideStoryRepository(ContentProviderBackedStoryRepository storyRepository) {
        return storyRepository;
    }
}
