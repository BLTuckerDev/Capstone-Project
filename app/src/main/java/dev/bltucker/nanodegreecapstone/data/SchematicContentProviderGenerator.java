package dev.bltucker.nanodegreecapstone.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import dev.bltucker.nanodegreecapstone.storydetail.data.CommentColumns;

@ContentProvider(authority = SchematicContentProviderGenerator.AUTHORITY,
        database = DatabaseGenerator.class,
        packageName = "dev.bltucker.nanodegreecapstone.data",
        name = "StoryProvider")
public final class SchematicContentProviderGenerator {

    public static final String AUTHORITY = "dev.bltucker.nanodegreecapstone.data";

    public static final String STORY_PATH = "stories";

    public static final String COMMENT_REFS_PATH = "commentRefs";

    public static final String COMMENTS_PATH = "comments";

    public static final String READ_LATER_STORY_PATH = "readLaterStories";

    public static final String ALL_COMMENTS_FOR_STORY_PATH = "storyComments";


    @TableEndpoint(table = DatabaseGenerator.STORIES)
    public static class StoryPaths {

        @ContentUri(path = STORY_PATH, type = "vnd.android.cursor.dir/list", defaultSort = StoryColumns.RANK)
        public static final Uri ALL_STORIES = Uri.parse("content://" + AUTHORITY + "/" + STORY_PATH);
    }

    @TableEndpoint(table = DatabaseGenerator.COMMENT_REFS)
    public static class CommentRefs {

        @ContentUri(path = COMMENT_REFS_PATH, type = "vnd.android.cursor.dir/list", defaultSort = CommentRefsColumns.READ_RANK)
        public static final Uri ALL_COMMENT_REFS = Uri.parse("content://" + AUTHORITY + "/" + COMMENT_REFS_PATH);

        @InexactContentUri(path = COMMENT_REFS_PATH + "/*",
                name = "STORY_ID",
                type = "vnd.android.cursor.dir/list",
                whereColumn = CommentRefsColumns.STORY_ID,
                defaultSort = CommentRefsColumns.READ_RANK,
                pathSegment = 1)
        public static Uri withStoryId(String id) {
            return Uri.withAppendedPath(ALL_COMMENT_REFS, id);
        }

    }


    @TableEndpoint(table = DatabaseGenerator.READ_LATER_STORIES)
    public static class ReadLaterStoryPaths {

        @ContentUri(path = READ_LATER_STORY_PATH, type = "vnd.android.cursor.dir/list")
        public static final Uri ALL_READ_LATER_STORIES = Uri.parse("content://" + AUTHORITY + "/" + READ_LATER_STORY_PATH);

        @InexactContentUri(path = READ_LATER_STORY_PATH + "/*",
                name = "STORY_ID",
                type = "vnd.android.cursor.dir/list",
                whereColumn = ReadLaterColumns._ID,
                pathSegment = 1)
        public static Uri withStoryId(String id) {
            return Uri.withAppendedPath(ALL_READ_LATER_STORIES, id);
        }
    }

    @TableEndpoint(table = DatabaseGenerator.COMMENTS)
    public static class CommentPaths{

        @ContentUri(path = COMMENTS_PATH, type = "vnd.android.cursor.dir/list")
        public static final Uri ALL_COMMENTS = Uri.parse("content://" + AUTHORITY + "/" + COMMENTS_PATH);

        @ContentUri(path = ALL_COMMENTS_FOR_STORY_PATH, type = "vnd.android.cursor.dir/list")
        public static final Uri ALL_COMMENTS_FOR_STORY_ID = Uri.parse("content://" + AUTHORITY + "/" + ALL_COMMENTS_FOR_STORY_PATH);

        @InexactContentUri(path = COMMENTS_PATH + "/*",
                name = "PARENT_ID",
                type = "vnd.android.cursor.dir/list",
                whereColumn = CommentColumns.PARENT_ID,
                pathSegment = 1,
                defaultSort = CommentColumns._ID)
        public static Uri withParentId(String id){
            return Uri.withAppendedPath(ALL_COMMENTS, id);
        }

        @InexactContentUri(
            path = ALL_COMMENTS_FOR_STORY_PATH + "/*",
            name = "STORY_ID",
            type = "vnd.android.cursor.dir/list",
            whereColumn = CommentColumns.STORY_ID,
            pathSegment = 1,
            defaultSort = CommentColumns._ID
        )
        public static Uri withStoryId(String storyId){
            return Uri.withAppendedPath(ALL_COMMENTS_FOR_STORY_ID, storyId);
        }


    }

}
