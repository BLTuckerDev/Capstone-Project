package dev.bltucker.nanodegreecapstone.models;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

import dev.bltucker.nanodegreecapstone.data.StoryColumns;

@SuppressWarnings({"squid:S1213"})
public class Story implements Parcelable {

    public static ContentValues mapToContentValues(Story aStory){

        ContentValues  cv = new ContentValues();

        cv.put(StoryColumns._ID, aStory.getId());
        cv.put(StoryColumns.POSTER_NAME, aStory.getPosterName());
        cv.put(StoryColumns.SCORE, aStory.getScore());
        cv.put(StoryColumns.TITLE, aStory.getTitle());
        cv.put(StoryColumns.UNIX_TIME, aStory.getUnixTime());
        cv.put(StoryColumns.URL, aStory.getUrl());
        cv.put(StoryColumns.RANK, aStory.getStoryRank());

        return cv;
    }


    private final long id;
    @SerializedName("by")
    private final String posterName;
    private final long score;
    @SerializedName("time")
    private final long unixTime;
    private final String title;
    private final String url;
    @SerializedName("kids")
    private final Long[] commentIds;

    private int storyRank = 0;

    public Story(long id, String posterName, long score, long unixTime, String title, String url, Long[] commentIdsParam){
        this.id = id;
        this.posterName = posterName;
        this.score = score;
        this.unixTime = unixTime;
        this.title = title;
        this.url = url;
        this.commentIds = Arrays.copyOf(commentIdsParam, commentIdsParam.length);
    }

    protected Story(Parcel input){
        id = input.readLong();
        posterName = input.readString();
        score = input.readLong();
        unixTime = input.readLong();
        title = input.readString();
        url = input.readString();
        final int commentIdsLength = input.readInt();

        long[] primitive = new long[commentIdsLength];
        input.readLongArray(primitive);

        Long[] objects = new Long[commentIdsLength];
        for (int i = 0; i < primitive.length; i++) {
            objects[i] = primitive[i];
        }

        commentIds = objects;
    }

    public long getId() {
        return id;
    }

    public String getPosterName() {
        return posterName;
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

    public Long[] getCommentIds() {
        if(null == commentIds){
            return new Long[0];
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

    @SuppressWarnings({"squid:S00121", "squid:S00122"})
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(posterName);
        dest.writeLong(score);
        dest.writeLong(unixTime);
        dest.writeString(title);
        dest.writeString(url);

        dest.writeInt(commentIds.length);
        long[] primitive = new long[commentIds.length];
        for (int i = 0; i < commentIds.length; i++) {
            primitive[i] = commentIds[i];
        }

        dest.writeLongArray(primitive);
    }


    public static final Parcelable.Creator<Story> CREATOR =
            new Parcelable.Creator<Story>(){
                public Story createFromParcel(Parcel in){
                    return new Story(in);
                }

                @Override
                public Story[] newArray(int size) {
                    return new Story[size];
                }
            };
}
