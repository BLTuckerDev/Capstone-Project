package dev.bltucker.nanodegreecapstone.models;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

import dev.bltucker.nanodegreecapstone.data.StoryColumns;

public final class Story {

    public static ContentValues mapToContentValues(Story aStory){

        ContentValues  cv = new ContentValues();

        cv.put(StoryColumns._ID, aStory.getId());
        cv.put(StoryColumns.AUTHOR_NAME, aStory.getAuthorName());
        cv.put(StoryColumns.SCORE, aStory.getScore());
        cv.put(StoryColumns.TITLE, aStory.getTitle());
        cv.put(StoryColumns.UNIX_TIME, aStory.getUnixTime());
        cv.put(StoryColumns.URL, aStory.getUrl());
        cv.put(StoryColumns.RANK, aStory.getStoryRank());

        return cv;
    }


    private final long id;
    @SerializedName("by")
    private final String authorName;
    private final long score;
    @SerializedName("time")
    private final long unixTime;
    private final String title;
    private final String url;
    @SerializedName("kids")
    private final long[] commentIds;

    private int storyRank = 0;

    public Story(long id, String authorName, long score, long unixTime, String title, String url, long[] commentIdsParam){
        this.id = id;
        this.authorName = authorName;
        this.score = score;
        this.unixTime = unixTime;
        this.title = title;
        this.url = url;
        this.commentIds = Arrays.copyOf(commentIdsParam, commentIdsParam.length);
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
        if(null == commentIds){
            return new long[0];
        } else {
            return Arrays.copyOf(commentIds, commentIds.length);
        }
    }

    public void setStoryRank(int rank){
        this.storyRank = rank;
    }

    public int getStoryRank(){
        return this.storyRank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Story story = (Story) o;

        return id == story.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
