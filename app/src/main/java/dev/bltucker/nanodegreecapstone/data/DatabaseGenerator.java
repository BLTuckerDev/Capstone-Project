package dev.bltucker.nanodegreecapstone.data;


import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

import dev.bltucker.nanodegreecapstone.storydetail.data.CommentColumns;

@Database(version = DatabaseGenerator.VERSION,
        packageName = "dev.bltucker.nanodegreecapstone.data",
        className = "StoryDatabase")
public final class DatabaseGenerator {

    public static final int VERSION = 1;

    @Table(StoryColumns.class) public static final String STORIES = "stories";

    @Table(CommentRefsColumns.class) public static final String COMMENT_REFS = "commentRefs";

    @Table(ReadLaterColumns.class) public static final String READ_LATER_STORIES = "readLaterStories";

    @Table(CommentColumns.class) public static final String COMMENTS = "comments";

    private DatabaseGenerator(){
    }
}
