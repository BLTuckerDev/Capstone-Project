package dev.bltucker.nanodegreecapstone.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;

public class ReadingSession {
    //todo collect analytics on the average number of comments a read story has
    public static final int INITIAL_COMMENT_CAPACITY = 50;
    public static final int INITIAL_STORY_CAPACITY = 150;
    //TODO convert to arrays where we can
    private Story currentStory;
    private List<Comment> currentStoryComments;

    private List<Story> storyList;

    private int largestStoryListIndexViewed;

    public ReadingSession(){
        currentStory = null;
        currentStoryComments = new ArrayList<>(INITIAL_COMMENT_CAPACITY);
        storyList = new ArrayList<>(INITIAL_STORY_CAPACITY);
        largestStoryListIndexViewed = 0;
    }

    public void setLargestStoryListIndexViewed(int index){
        largestStoryListIndexViewed = index;
    }

    public int getLargestStoryListIndexViewed(){
        return largestStoryListIndexViewed;
    }

    public void read(Story selectedStory, List<Comment> selectedStoryComments){
        currentStory = selectedStory;
        currentStoryComments.clear();
        currentStoryComments.addAll(selectedStoryComments);
    }

    public void setStories(List<Story> stories){
        storyList.clear();
        largestStoryListIndexViewed = 0;
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
}
