package dev.bltucker.nanodegreecapstone.data;

import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface ReadLaterColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    String _ID = "_id";

    @DataType(DataType.Type.TEXT) String POSTER_NAME = "authorName";

    @DataType(DataType.Type.TEXT) String TITLE = "title";

    @DataType(DataType.Type.TEXT) String URL = "url";
}
