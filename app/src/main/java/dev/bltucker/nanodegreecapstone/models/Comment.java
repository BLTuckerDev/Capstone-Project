package dev.bltucker.nanodegreecapstone.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings({"squid:S1213"})
@Entity(tableName = "comments", indices = {@Index(value = "storyId")})
public final class Comment implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    public final long id;

    public final long storyId;

    public final String authorName;

    public final String commentText;

    public final long unixPostTime;

    public final long parentId;

    public final int depth;


    public Comment(long id,
                   long storyId,
                   String authorName,
                   String commentText,
                   long unixPostTime,
                   long parentId,
                   int depth) {
        this.id = id;
        this.storyId = storyId;
        this.authorName = authorName != null ? authorName : "";
        this.commentText = commentText != null ? commentText : "";
        this.unixPostTime = unixPostTime;
        this.parentId = parentId;
        this.depth = depth;
    }

    public Comment(Parcel in) {
        this.storyId = in.readLong();
        this.id = in.readLong();
        this.authorName = in.readString();
        this.commentText = in.readString();
        this.unixPostTime = in.readLong();
        this.parentId = in.readLong();
        this.depth = in.readInt();
    }

    public long getStoryId() {
        return storyId;
    }

    public long getId() {
        return id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getCommentText() {
        return commentText;
    }

    public long getUnixPostTime() {
        return unixPostTime;
    }

    public long getParentId() {
        return parentId;
    }

    public int getDepth() {
        return this.depth;
    }

    @SuppressWarnings({"squid:S00121", "squid:S00122"})
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        return id == comment.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.storyId);
        dest.writeLong(this.id);
        dest.writeString(this.authorName);
        dest.writeString(this.commentText);
        dest.writeLong(this.unixPostTime);
        dest.writeLong(this.parentId);
        dest.writeInt(this.depth);
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
