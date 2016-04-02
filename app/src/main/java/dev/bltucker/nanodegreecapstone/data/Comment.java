package dev.bltucker.nanodegreecapstone.data;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public final class Comment {

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
        this.authorName = authorName;
        this.commentText = commentText;
        this.unixPostTime = unixPostTime;
        this.replyIds = Arrays.copyOf(replyIdsParam, replyIdsParam.length);
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
}
