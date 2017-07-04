package dev.bltucker.nanodegreecapstone.models;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;


import dev.bltucker.nanodegreecapstone.storydetail.data.CommentColumns;

@SuppressWarnings({"squid:S1213"})
public final class Comment implements Parcelable {

    public static ContentValues mapToContentValues(Comment aComment) {
        ContentValues cv = new ContentValues();

        cv.put(CommentColumns.STORY_ID, aComment.getStoryId());
        cv.put(CommentColumns.COMMENT_ID, aComment.getId());
        cv.put(CommentColumns.AUTHOR_NAME, aComment.getAuthorName());
        cv.put(CommentColumns.COMMENT_TEXT, aComment.getCommentText());
        cv.put(CommentColumns.UNIX_POST_TIME, aComment.getUnixPostTime());
        cv.put(CommentColumns.PARENT_ID, aComment.getParentId());
        cv.put(CommentColumns.COMMENT_DEPTH, aComment.getDepth());

        return cv;
    }

    private final long storyId;

    private final long id;

    private final String authorName;

    private final String commentText;

    private final long unixPostTime;

    private final long parentId;

    private final int depth;

    public Comment(long storyId, long id, String authorName, String commentText,
                   long unixPostTime, long parentId, int commentDepth){
        this.storyId = storyId;
        this.id = id;
        this.authorName = authorName != null ? authorName : "";
        this.commentText = commentText != null ? commentText : "";
        this.unixPostTime = unixPostTime;
        this.parentId = parentId;
        this.depth = commentDepth;
    }

    public Comment(Parcel in){
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

    public int getDepth(){
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
