package dev.bltucker.nanodegreecapstone.common.injection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.NotificationManager;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

import java.util.Calendar;
import java.util.TimeZone;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.BuildConfig;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.R;
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.GregorianUTC;
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.IO;
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.StoryMax;
import dev.bltucker.nanodegreecapstone.common.data.ContentProviderBackedStoryRepository;
import dev.bltucker.nanodegreecapstone.common.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.common.data.HackerNewsDatabase;
import dev.bltucker.nanodegreecapstone.common.data.StoryProvider;
import dev.bltucker.nanodegreecapstone.common.data.StoryRepository;
import dev.bltucker.nanodegreecapstone.common.data.daos.CommentRefsDao;
import dev.bltucker.nanodegreecapstone.common.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.common.data.daos.ReadLaterStoryDao;
import dev.bltucker.nanodegreecapstone.common.data.daos.StoryDao;
import dev.bltucker.nanodegreecapstone.common.data.migrations.Version1to2;
import dev.bltucker.nanodegreecapstone.common.data.migrations.Version2to3;
import io.reactivex.Scheduler;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static dev.bltucker.nanodegreecapstone.common.data.StoryProvider.ALL_COMMENTS_STORY_PATH_CODE;
import static dev.bltucker.nanodegreecapstone.common.data.StoryProvider.ALL_STORIES_PATH_CODE;
import static dev.bltucker.nanodegreecapstone.common.data.StoryProvider.COMMENTS_PATH_CODE;
import static dev.bltucker.nanodegreecapstone.common.data.StoryProvider.COMMENTS_WITH_PARENT_PATH_CODE;
import static dev.bltucker.nanodegreecapstone.common.data.StoryProvider.COMMENT_REFS_PATH_CODE;
import static dev.bltucker.nanodegreecapstone.common.data.StoryProvider.COMMENT_REFS_PATH_ITEM_CODE;
import static dev.bltucker.nanodegreecapstone.common.data.StoryProvider.READ_LATER_STORIES_PATH_CODE;
import static dev.bltucker.nanodegreecapstone.common.data.StoryProvider.READ_LATER_STORIES_PATH_ITEM_CODE;

@Module
public class ApplicationResourcesModule {

    protected final CapstoneApplication application;

    public ApplicationResourcesModule(CapstoneApplication application) {
        this.application = application;
    }

    @Provides
    @ApplicationScope
    HackerNewsDatabase providesHackerNewsDatabase() {
        return Room.databaseBuilder(application, HackerNewsDatabase.class, "databaseGenerator.db")
                .addMigrations(new Version1to2(1, 2))
                .addMigrations(new Version2to3(2, 3))
                .build();
    }

    @Provides
    @ApplicationScope
    StoryDao provideStoryDao(HackerNewsDatabase hackerNewsDatabase) {
        return hackerNewsDatabase.storyDao();
    }

    @Provides
    @ApplicationScope
    CommentRefsDao provideCommentRefsDao(HackerNewsDatabase hackerNewsDatabase) {
        return hackerNewsDatabase.commentRefsDao();
    }

    @Provides
    @ApplicationScope
    ReadLaterStoryDao provideReadLaterStoryDao(HackerNewsDatabase hackerNewsDatabase) {
        return hackerNewsDatabase.readLaterStoryDao();
    }

    @Provides
    @ApplicationScope
    CommentsDao provideCommentsDao(HackerNewsDatabase hackerNewsDatabase) {
        return hackerNewsDatabase.commentsDao();
    }

    @Provides
    UriMatcher provideContentProviderUriMatcher() {
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
    @GregorianUTC
    public Calendar provideCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
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
    public HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return httpLoggingInterceptor;
    }

    @Provides
    @ApplicationScope
    public OkHttpClient okHttpClient(HttpLoggingInterceptor interceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if(BuildConfig.DEBUG){
//            builder.addInterceptor(interceptor);
        }

        return builder.build();
    }

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
    public HackerNewsApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(HackerNewsApiService.class);
    }

    @Provides
    @ApplicationScope
    public StoryRepository provideStoryRepository(ContentProviderBackedStoryRepository storyRepository) {
        return storyRepository;
    }

    @Provides
    @ApplicationScope
    public FirebaseJobDispatcher provideFirebaseJobDispatcher(){
        return new FirebaseJobDispatcher(new GooglePlayDriver(application));
    }
}
