package dev.bltucker.nanodegreecapstone.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "commentRefs", indices = {@Index(value = "storyId")})
public class CommentReference {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    public final long id;

    public final long storyId;

    public final long readRank;

    public CommentReference(long id, long storyId, long readRank) {
        this.id = id;
        this.storyId = storyId;
        this.readRank = readRank;
    }

}
