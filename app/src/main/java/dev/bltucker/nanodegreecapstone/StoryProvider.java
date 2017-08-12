package dev.bltucker.nanodegreecapstone;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.daos.CommentRefsDao;
import dev.bltucker.nanodegreecapstone.data.daos.CommentsDao;
import dev.bltucker.nanodegreecapstone.data.daos.ReadLaterStoryDao;
import dev.bltucker.nanodegreecapstone.data.daos.StoryDao;
import dev.bltucker.nanodegreecapstone.injection.DaggerApplicationComponent;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;

@Keep
public class StoryProvider extends ContentProvider {

    public static final String AUTHORITY = "dev.bltucker.nanodegreecapstone.data";

    public static final String ALL_STORIES_PATH = "stories";

    public static final String COMMENT_REFS_PATH = "commentRefs";

    public static final String COMMENTS_PATH = "comments";

    public static final String READ_LATER_STORY_PATH = "readLaterStories";

    public static final String ALL_COMMENTS_FOR_STORY_PATH = "storyComments";


    public static final int ALL_STORIES_PATH_CODE = 0;

    public static final int COMMENT_REFS_PATH_ITEM_CODE = 1;

    public static final int COMMENT_REFS_PATH_CODE = 2;

    public static final int COMMENTS_PATH_CODE = 3;

    public static final int COMMENTS_WITH_PARENT_PATH_CODE = 4;

    public static final int ALL_COMMENTS_STORY_PATH_CODE = 5;

    public static final int READ_LATER_STORIES_PATH_CODE = 6;

    public static final int READ_LATER_STORIES_PATH_ITEM_CODE = 7;




    public static final Uri STORIES_URI = Uri.parse("content://" + AUTHORITY + "/" + ALL_STORIES_PATH);

    public static final Uri COMMENT_REFS_URI = Uri.parse("content://" + AUTHORITY + "/" + COMMENT_REFS_PATH);

    public static final Uri COMMENTS_URI = Uri.parse("content://" + AUTHORITY + "/" + COMMENTS_PATH);

    public static final Uri READ_LATER_URI = Uri.parse("content://" + AUTHORITY + "/" + READ_LATER_STORY_PATH);

    public static final Uri STORY_COMMENTS_PATH = Uri.parse("content://" + AUTHORITY + "/" + ALL_COMMENTS_FOR_STORY_PATH);


    private boolean hasBeenInjected = false;

    @Inject
    UriMatcher uriMatcher;

    @Inject
    StoryDao storyDao;

    @Inject
    CommentRefsDao commentRefsDao;

    @Inject
    ReadLaterStoryDao readLaterStoryDao;

    @Inject
    CommentsDao commentsDao;

    @Inject
    ContentResolver contentResolver;

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        if(!hasBeenInjected){
            inject();
        }

        Cursor queryCursor;

        switch(uriMatcher.match(uri)){
            case ALL_STORIES_PATH_CODE:
                queryCursor = storyDao.getAllStoriesCursor();
                break;

            case READ_LATER_STORIES_PATH_CODE:
                queryCursor = readLaterStoryDao.getAllReadLaterStoriesCursor();
                break;


            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }

        queryCursor.setNotificationUri(contentResolver, uri);

        return queryCursor;
    }

    private void inject() {
        DaggerInjector.getApplicationComponent().inject(this);
        hasBeenInjected = true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(uriMatcher.match(uri)){
            case ALL_STORIES_PATH_CODE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + "stories";

            case COMMENT_REFS_PATH_ITEM_CODE:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + "commentRefs";

            case COMMENT_REFS_PATH_CODE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + "commentRefs";

            case COMMENTS_PATH_CODE:
            case COMMENTS_WITH_PARENT_PATH_CODE:
            case ALL_COMMENTS_STORY_PATH_CODE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + "comments";


            case READ_LATER_STORIES_PATH_CODE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + "readLaterStories";

            case READ_LATER_STORIES_PATH_ITEM_CODE:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + "readLaterStories";

            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if(!hasBeenInjected){
            inject();
        }

        switch(uriMatcher.match(uri)){
            case ALL_STORIES_PATH_CODE:
                break;
            case COMMENT_REFS_PATH_ITEM_CODE:
                break;
            case COMMENT_REFS_PATH_CODE:
                break;

            case COMMENTS_PATH_CODE:
                break;

            case COMMENTS_WITH_PARENT_PATH_CODE:
                break;

            case ALL_COMMENTS_STORY_PATH_CODE:
                break;

            case READ_LATER_STORIES_PATH_CODE:
                break;

            case READ_LATER_STORIES_PATH_ITEM_CODE:
                break;

            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }

        return null;


    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        if(!hasBeenInjected){
            inject();
        }

        switch(uriMatcher.match(uri)){
            case ALL_STORIES_PATH_CODE:
                break;
            case COMMENT_REFS_PATH_ITEM_CODE:
                break;
            case COMMENT_REFS_PATH_CODE:
                break;

            case COMMENTS_PATH_CODE:
                break;

            case COMMENTS_WITH_PARENT_PATH_CODE:
                break;

            case ALL_COMMENTS_STORY_PATH_CODE:
                break;

            case READ_LATER_STORIES_PATH_CODE:
                break;

            case READ_LATER_STORIES_PATH_ITEM_CODE:
                break;

            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }

        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        if(!hasBeenInjected){
            inject();
        }

        switch(uriMatcher.match(uri)){
            case ALL_STORIES_PATH_CODE:
                break;
            case COMMENT_REFS_PATH_ITEM_CODE:
                break;
            case COMMENT_REFS_PATH_CODE:
                break;

            case COMMENTS_PATH_CODE:
                break;

            case COMMENTS_WITH_PARENT_PATH_CODE:
                break;

            case ALL_COMMENTS_STORY_PATH_CODE:
                break;

            case READ_LATER_STORIES_PATH_CODE:
                break;

            case READ_LATER_STORIES_PATH_ITEM_CODE:
                break;

            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }

        return 0;
    }
}
