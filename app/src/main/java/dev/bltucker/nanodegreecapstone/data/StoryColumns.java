package dev.bltucker.nanodegreecapstone.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface StoryColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey String _ID = "_id";

    @DataType(DataType.Type.TEXT) String POSTER_NAME = "authorName";

    @DataType(DataType.Type.INTEGER) String SCORE = "score";

    @DataType(DataType.Type.INTEGER) String UNIX_TIME = "unixTime";

    @DataType(DataType.Type.TEXT) String TITLE = "title";

    @DataType(DataType.Type.TEXT) String URL = "url";

    @DataType(DataType.Type.INTEGER) String RANK = "rank";

}
