package dev.bltucker.nanodegreecapstone.storydetail.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

@SuppressWarnings({"squid:S1214", "squid:S00115"})
public interface CommentColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey(onConflict = ConflictResolutionType.REPLACE) @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @Unique(onConflict = ConflictResolutionType.REPLACE)
    String COMMENT_ID = "commentId";

    @DataType(DataType.Type.TEXT)
    String AUTHOR_NAME = "authorName";

    @DataType(DataType.Type.TEXT)
    String COMMENT_TEXT = "commentText";

    @DataType(DataType.Type.INTEGER)
    String UNIX_POST_TIME = "unixPostTime";

    @DataType(DataType.Type.INTEGER)
    String PARENT_ID = "parentId";

    @DataType(DataType.Type.INTEGER)
    String COMMENT_DEPTH = "commentDepth";

    @DataType(DataType.Type.INTEGER)
    String STORY_ID = "storyId";
}
