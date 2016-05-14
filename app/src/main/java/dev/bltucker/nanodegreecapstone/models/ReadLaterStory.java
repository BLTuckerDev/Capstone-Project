package dev.bltucker.nanodegreecapstone.models;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import dev.bltucker.nanodegreecapstone.data.ReadLaterColumns;

public class ReadLaterStory implements Parcelable{

    public static ContentValues mapToContentValues(ReadLaterStory readLaterStory){
        ContentValues cv = new ContentValues();

        cv.put(ReadLaterColumns._ID, readLaterStory.getId());
        cv.put(ReadLaterColumns.POSTER_NAME, readLaterStory.getPosterName());
        cv.put(ReadLaterColumns.TITLE, readLaterStory.getTitle());
        cv.put(ReadLaterColumns.URL, readLaterStory.getUrl());

        return cv;
    }

    private final long id;
    private final String posterName;
    private final String title;
    private final String url;

    public ReadLaterStory(long id, String posterName, String title, String url){
        this.id = id;
        this.posterName = posterName;
        this.title = title;
        this.url = url;
    }

    protected ReadLaterStory(Parcel in){
        id = in.readLong();
        posterName = in.readString();
        title = in.readString();
        url = in.readString();
    }

    public long getId() {
        return id;
    }

    public String getPosterName() {
        return posterName;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReadLaterStory that = (ReadLaterStory) o;

        if (id != that.id) return false;
        if (!posterName.equals(that.posterName)) return false;
        if (!title.equals(that.title)) return false;
        return url.equals(that.url);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + posterName.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(posterName);
        dest.writeString(title);
        dest.writeString(url);
    }

    public static final Parcelable.Creator<ReadLaterStory> CREATOR =
            new Parcelable.Creator<ReadLaterStory>(){
                @Override
                public ReadLaterStory createFromParcel(Parcel source) {
                    return new ReadLaterStory(source);
                }

                @Override
                public ReadLaterStory[] newArray(int size) {
                    return new ReadLaterStory[size];
                }
            };
}
