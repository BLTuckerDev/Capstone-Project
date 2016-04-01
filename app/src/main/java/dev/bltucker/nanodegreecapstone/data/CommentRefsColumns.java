package dev.bltucker.nanodegreecapstone.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface CommentRefsColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey String _ID = "_id";

    @DataType(DataType.Type.INTEGER) String STORY_ID = "storyId";
}
