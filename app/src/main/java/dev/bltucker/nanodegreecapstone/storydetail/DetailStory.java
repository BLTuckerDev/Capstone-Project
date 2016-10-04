package dev.bltucker.nanodegreecapstone.storydetail;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import timber.log.Timber;

//TODO figure out how to do this with just Rx
public class DetailStory extends Observable implements Parcelable {

    @Nullable
    private final Story story;

    private final List<Comment> commentList =  new ArrayList<>();

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

    public void addComments(List<Comment> comments){
        Timber.d("Adding %d comments to the detail story", comments.size());
        List<Comment> oldComments = new ArrayList<>(this.commentList);
        this.commentList.clear();
        this.commentList.addAll(comments);

        if(oldComments.isEmpty() && comments.isEmpty()){
            return;
        }
        setChanged();
        notifyObservers(new DetailStoryChangeEvent(oldComments, this.commentList));
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
