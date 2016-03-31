package dev.bltucker.nanodegreecapstone.data;

import java.util.Arrays;

public final class Comment {

    private final long id;
    private final String authorName;
    private final String commentText;
    private final long unixPostTime;
    private final long[] replyIds;

    public Comment(long id, String authorName, String commentText, long unixPostTime, long[] replyIds){
        this.id = id;
        this.authorName = authorName;
        this.commentText = commentText;
        this.unixPostTime = unixPostTime;
        this.replyIds = replyIds;
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
        return Arrays.copyOf(replyIds, replyIds.length);
    }
}
