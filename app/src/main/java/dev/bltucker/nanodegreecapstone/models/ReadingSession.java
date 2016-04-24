package dev.bltucker.nanodegreecapstone.models;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.injection.StoryMax;
import timber.log.Timber;

public class ReadingSession {
    //todo collect analytics on the average number of comments a read story has
    private static final int INITIAL_COMMENT_CAPACITY = 30;
    //TODO convert to arrays where we can
    private Story currentStory;
    private List<Comment> currentStoryComments;
    private Map<Long, Comment> commentIdToParentMap;

    private List<Story> storyList;
    private boolean storyListIsDirty = false;

    @Inject
    public ReadingSession(@StoryMax int maximumStoryCount){
        currentStory = null;
        currentStoryComments = new ArrayList<>(INITIAL_COMMENT_CAPACITY);
        commentIdToParentMap = new HashMap<>(INITIAL_COMMENT_CAPACITY);
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

    public List<Story> getStories(){
        return Collections.unmodifiableList(storyList);
    }

    public Story getCurrentStory(){
        return currentStory;
    }

    public List<Comment> getCurrentStoryComments(){
        return Collections.unmodifiableList(currentStoryComments);
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
}
