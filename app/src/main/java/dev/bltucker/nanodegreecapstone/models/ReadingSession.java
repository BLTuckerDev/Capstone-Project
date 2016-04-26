package dev.bltucker.nanodegreecapstone.models;

import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.injection.StoryMax;
import timber.log.Timber;

public class ReadingSession {
    private static final int INITIAL_COMMENT_CAPACITY = 30;

    private Story currentStory;
    private final List<Comment> currentStoryComments;
    private final SimpleArrayMap<Long, Comment> commentIdToParentMap;
    private final List<Story> storyList;
    private boolean storyListIsDirty = false;

    @Inject
    public ReadingSession(@StoryMax int maximumStoryCount){
        currentStory = null;
        currentStoryComments = new ArrayList<>(INITIAL_COMMENT_CAPACITY);
        commentIdToParentMap = new SimpleArrayMap<>(INITIAL_COMMENT_CAPACITY);
        storyList = new ArrayList<>(maximumStoryCount);
    }

    public void read(Story selectedStory, List<Comment> selectedStoryComments){
        currentStory = selectedStory;
        currentStoryComments.clear();
        commentIdToParentMap.clear();
        currentStoryComments.addAll(selectedStoryComments);

        for (int i = 0; i < selectedStoryComments.size(); i++) {
            Comment currentComment = selectedStoryComments.get(i);
            long[] childComments = currentComment.getReplyIds();
            for (int j = 0; j < childComments.length; j++) {
                commentIdToParentMap.put(childComments[j], currentComment);
            }
        }
    }

    public void setStories(List<Story> stories){
        Timber.d("ReadingSessions stories are being updated");
        storyList.clear();
        storyList.addAll(stories);
    }

    public boolean hasStories(){
        return !storyList.isEmpty();
    }

    public Story getCurrentStory(){
        return currentStory;
    }

    public boolean isStoryListIsDirty() {
        return storyListIsDirty;
    }

    public void setStoryListIsDirty(boolean storyListIsDirty) {
        this.storyListIsDirty = storyListIsDirty;
    }

    @Nullable
    public Comment getParentComment(long commentId){
        return commentIdToParentMap.get(commentId);
    }

    @Nullable
    public Story getStory(int position) {
        if(position < 0 || position >= storyList.size()){
            return null;
        }
        return storyList.get(position);
    }

    public int storyCount() {
        return storyList.size();
    }

    @Nullable
    public Comment getCurrentStoryComment(int position) {
        if(position < 0 || position >= currentStoryComments.size()){
            return null;
        }
        return currentStoryComments.get(position);
    }

    public int currentStoryCommentCount() {
        return currentStoryComments.size();
    }
}
