package dev.bltucker.nanodegreecapstone.data;

import java.util.Arrays;

public final class Story {

    private final long id;
    private final String authorName;
    private final long score;
    private final long unixTime;
    private final String title;
    private final String url;
    private final long[] commentIds;

    public Story(long id, String authorName, long score, long unixTime, String title, String url, long[] commentIds){

        this.id = id;
        this.authorName = authorName;
        this.score = score;
        this.unixTime = unixTime;
        this.title = title;
        this.url = url;
        this.commentIds = commentIds;
    }

    public long getId() {
        return id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public long getScore() {
        return score;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public long[] getCommentIds() {
        return Arrays.copyOf(commentIds, commentIds.length);
    }
}
