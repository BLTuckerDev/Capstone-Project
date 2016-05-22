package dev.bltucker.nanodegreecapstone.storydetail;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;

public class DetailStory implements Parcelable {

    private final Story story;

    private final List<Comment> commentList;

    public DetailStory(Story story){
        this.story = story;
        this.commentList = new ArrayList<>();
    }

    public DetailStory(Parcel input){
        this.commentList = new ArrayList<>();
        story = input.readParcelable(Story.class.getClassLoader());
        input.readTypedList(commentList, Comment.CREATOR);
    }

    public long getStoryId(){
        return story.getId();
    }

    public String getPosterName(){
        return story.getPosterName();
    }

    public long getScore(){
        return story.getScore();
    }

    public String getTitle(){
        return story.getTitle();
    }

    public String getUrl(){
        return story.getUrl();
    }

    public Long[] getCommentIds(){
        return story.getCommentIds();
    }

    public List<Comment> getUnmodifiableCommentList(){
        return Collections.unmodifiableList(this.commentList);
    }

    public void setComments(List<Comment> comments){
        this.commentList.clear();
        this.commentList.addAll(comments);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(story, 0);
        dest.writeTypedList(commentList);
    }

    public static final Creator<DetailStory> CREATOR = new Creator<DetailStory>() {
        @Override
        public DetailStory createFromParcel(Parcel in) {
            return new DetailStory(in);
        }

        @Override
        public DetailStory[] newArray(int size) {
            return new DetailStory[size];
        }
    };
}
