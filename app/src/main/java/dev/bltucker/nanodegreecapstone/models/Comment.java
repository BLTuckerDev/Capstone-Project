package dev.bltucker.nanodegreecapstone.models;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import dev.bltucker.nanodegreecapstone.storydetail.data.CommentColumns;

public final class Comment implements Parcelable {

    public static ContentValues mapToContentValues(Comment aComment) {
        ContentValues cv = new ContentValues();

        cv.put(CommentColumns._ID, aComment.getId());
        cv.put(CommentColumns.AUTHOR_NAME, aComment.getAuthorName());
        cv.put(CommentColumns.COMMENT_TEXT, aComment.getCommentText());
        cv.put(CommentColumns.UNIX_POST_TIME, aComment.getUnixPostTime());
        cv.put(CommentColumns.PARENT_ID, aComment.getParentId());

        return cv;
    }

    private final long id;
    @SerializedName("by")
    private final String authorName;
    @SerializedName("text")
    private final String commentText;
    @SerializedName("time")
    private final long unixPostTime;
    @SerializedName("parent")
    private final long parentId;

    public Comment(long id, String authorName, String commentText,
                   long unixPostTime, long parentId){
        this.id = id;
        this.authorName = authorName != null ? authorName : "";
        this.commentText = commentText != null ? commentText : "";
        this.unixPostTime = unixPostTime;
        this.parentId = parentId;
    }

    public Comment(Parcel in){
        this.id = in.readLong();
        this.authorName = in.readString();
        this.commentText = in.readString();
        this.unixPostTime = in.readLong();
        this.parentId = in.readLong();
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
        dest.writeLong(this.id);
        dest.writeString(this.authorName);
        dest.writeString(this.commentText);
        dest.writeLong(unixPostTime);
        dest.writeLong(this.parentId);
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
