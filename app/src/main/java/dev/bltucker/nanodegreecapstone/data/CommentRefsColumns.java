package dev.bltucker.nanodegreecapstone.data;

import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface CommentRefsColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey(onConflict = ConflictResolutionType.REPLACE) String _ID = "_id";

    @DataType(DataType.Type.INTEGER) String STORY_ID = "storyId";

    @DataType(DataType.Type.INTEGER) String READ_RANK = "readRank";
}
