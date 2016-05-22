package dev.bltucker.nanodegreecapstone.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public final class Comment implements Parcelable {

    private final long id;
    @SerializedName("by")
    private final String authorName;
    @SerializedName("text")
    private final String commentText;
    @SerializedName("time")
    private final long unixPostTime;
    @SerializedName("kids")
    private final long[] replyIds;

    public Comment(long id, String authorName, String commentText, long unixPostTime, long[] replyIdsParam){
        this.id = id;
        this.authorName = authorName != null ? authorName : "";
        this.commentText = commentText != null ? commentText : "";
        this.unixPostTime = unixPostTime;
        this.replyIds = Arrays.copyOf(replyIdsParam, replyIdsParam.length);
    }

    public Comment(Parcel in){
        this.id = in.readLong();
        this.authorName = in.readString();
        this.commentText = in.readString();
        this.unixPostTime = in.readLong();
        int replyIdLength = in.readInt();
        this.replyIds = new long[replyIdLength];
        in.readLongArray(this.replyIds);
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

    public long[] getReplyIds() {
        if(null == replyIds){
            return new long[0];
        }
        return Arrays.copyOf(replyIds, replyIds.length);
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
        dest.writeInt(replyIds.length);
        dest.writeLongArray(replyIds);
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
