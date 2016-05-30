package dev.bltucker.nanodegreecapstone.storydetail;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;

public class DetailStory implements Parcelable {

    @Nullable
    private final Story story;

    private final List<Comment> commentList =  new ArrayList<>();
    private final SimpleArrayMap<Long, Comment> commentIdToParentMap = new SimpleArrayMap<>();

    public DetailStory(@Nullable Story story){
        this.story = story;
    }

    public DetailStory(Parcel input){
        story = input.readParcelable(Story.class.getClassLoader());
        input.readTypedList(commentList, Comment.CREATOR);
    }

    public long getStoryId(){
        assert story != null;
        return story.getId();
    }

    public String getPosterName(){
        assert story != null;
        return story.getPosterName();
    }

    public long getScore(){
        assert story != null;
        return story.getScore();
    }

    public String getTitle(){
        assert story != null;
        return story.getTitle();
    }

    public String getUrl(){
        assert story != null;
        return story.getUrl();
    }

    public Long[] getCommentIds(){
        assert story != null;
        return story.getCommentIds();
    }

    public List<Comment> getUnmodifiableCommentList(){
        return Collections.unmodifiableList(this.commentList);
    }

    public int getCommentCount(){
        return commentList.size();
    }

    @Nullable
    public Comment getComment(int index){
        if(index < 0 || index >= commentList.size()){
            return null;
        }
        return commentList.get(index);
    }

    public void setComments(List<Comment> comments){
        this.commentList.clear();
        commentIdToParentMap.clear();
        this.commentList.addAll(comments);

        for (int i = 0; i < commentList.size(); i++) {
            Comment currentComment = commentList.get(i);
            long[] childComments = currentComment.getReplyIds();
            for (int j = 0; j < childComments.length; j++) {
                commentIdToParentMap.put(childComments[j], currentComment);
            }
        }
    }

    @Nullable
    public Comment getParentComment(long commentId){
        assert story != null;
        return commentIdToParentMap.get(commentId);
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

    public boolean hasStory() {
        return story != null;
    }
}
