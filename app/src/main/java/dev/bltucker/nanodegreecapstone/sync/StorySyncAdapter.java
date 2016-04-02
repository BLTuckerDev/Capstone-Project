package dev.bltucker.nanodegreecapstone.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.CommentRefsColumns;
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.data.SchematicContentProviderGenerator;
import dev.bltucker.nanodegreecapstone.data.Story;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public final class StorySyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACCOUNT = "storySyncAccount";

    public static final String ACCOUNT_TYPE = "bltucker.dev";

    private final ContentResolver contentResolver;

//    @Inject
//    HackerNewsApiService apiService;

    public StorySyncAdapter(Context context, boolean autoInitialize){
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
        //TODO inject
    }

    public StorySyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        contentResolver = context.getContentResolver();
        //TODO inject
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hacker-news.firebaseio.com/v0/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        final HackerNewsApiService apiService = retrofit.create(HackerNewsApiService.class);


        Timber.d("Syncing....");

        apiService.getTopStoryIds()
                .concatMap(new Func1<List<Long>, Observable<List<Observable<Story>>>>() {
                    @Override
                    public Observable<List<Observable<Story>>> call(List<Long> storyIds) {
                        List<Observable<Story>> observableStoryList = new ArrayList<>(storyIds.size());
                        for (int i = 0; i < storyIds.size(); i++) {
                            observableStoryList.add(apiService.getStory(storyIds.get(i)));
                        }
                        return Observable.just(observableStoryList);
                    }
                })
                .subscribe(new Subscriber<List<Observable<Story>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error downloading top stories");
                    }

                    @Override
                    public void onNext(List<Observable<Story>> observables) {
                        List<ContentValues> storyContentValues = new ArrayList<>(observables.size());
                        List<ContentValues> commentRefsContentValuesList = new ArrayList<>();

                        for (int i = 0; i < observables.size(); i++) {
                            Story story = observables.get(i).toBlocking().first();
                            storyContentValues.add(Story.mapToContentValues(story));
                            commentRefsContentValuesList.addAll(getCommentRefList(story));
                        }

                        ContentValues[] contentValues = new ContentValues[storyContentValues.size()];
                        int insertedStoryCount = getContext().getContentResolver().bulkInsert(SchematicContentProviderGenerator.StoryPaths.ALL_STORIES, storyContentValues.toArray(contentValues));

                        Timber.d("Inserted %d stories", insertedStoryCount);

                        ContentValues[] commentRefsContentValuesArray = new ContentValues[commentRefsContentValuesList.size()];
                        int commentRefInsertCount = getContext().getContentResolver().bulkInsert(SchematicContentProviderGenerator.CommentRefs.ALL_COMMENTS, commentRefsContentValuesList.toArray(commentRefsContentValuesArray));

                        Timber.d("Inserted %d comment references", commentRefInsertCount);

                    }
                });
    }



    private List<ContentValues> getCommentRefList(Story story){
        long[] commentIds = story.getCommentIds();
        List<ContentValues> contentValues = new ArrayList<>(commentIds.length);

        for (int i = 0; i < commentIds.length; i++) {
            ContentValues aContentValue = new ContentValues();
            aContentValue.put(CommentRefsColumns._ID,  commentIds[i]);
            aContentValue.put(CommentRefsColumns.STORY_ID, story.getId());
            contentValues.add(aContentValue);
        }

        return contentValues;
    }


}
