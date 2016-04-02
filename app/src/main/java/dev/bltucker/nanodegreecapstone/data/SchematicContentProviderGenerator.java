package dev.bltucker.nanodegreecapstone.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = SchematicContentProviderGenerator.AUTHORITY,
database = DatabaseGenerator.class,
packageName = "dev.bltucker.nanodegreecapstone.data",
name = "StoryProvider")
public final class SchematicContentProviderGenerator {

    public static final String AUTHORITY = "dev.bltucker.nanodegreecapstone.data";

    public static final String STORY_PATH = "stories";

    public static final String COMMENTS_PATH = "comments";


    @TableEndpoint(table = DatabaseGenerator.STORIES)
    public static class StoryPaths{

        @ContentUri(path = STORY_PATH, type = "vnd.android.cursor.dir/list", defaultSort = StoryColumns.SCORE)
        public static final Uri ALL_STORIES = Uri.parse("content://" + AUTHORITY + "/" + STORY_PATH);
    }

    @TableEndpoint(table = DatabaseGenerator.COMMENT_REFS)
    public static class CommentRefs{

        @ContentUri(path = COMMENTS_PATH, type = "vnd.android.cursor.dir/list")
        public static final Uri ALL_COMMENTS = Uri.parse("content://" + AUTHORITY + "/" + COMMENTS_PATH);

        @InexactContentUri(path = COMMENTS_PATH + "/*",
        name = "STORY_ID",
        type = "vnd.android.cursor.dir/list",
        whereColumn = CommentRefsColumns.STORY_ID,
        pathSegment = 1)
        public static Uri withStoryId(String id){
            return Uri.withAppendedPath(ALL_COMMENTS, id);
        }

    }

}
