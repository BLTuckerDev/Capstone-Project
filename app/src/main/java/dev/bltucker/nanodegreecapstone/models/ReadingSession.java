package dev.bltucker.nanodegreecapstone.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.injection.StoryMax;
import timber.log.Timber;

public class ReadingSession {
    //todo collect analytics on the average number of comments a read story has
    private static final int INITIAL_COMMENT_CAPACITY = 50;
    //TODO convert to arrays where we can
    private Story currentStory;
    private List<Comment> currentStoryComments;

    private List<Story> storyList;

    @Inject
    public ReadingSession(@StoryMax int maximumStoryCount){
        currentStory = null;
        currentStoryComments = new ArrayList<>(INITIAL_COMMENT_CAPACITY);
        storyList = new ArrayList<>(maximumStoryCount);
    }

    public void read(Story selectedStory, List<Comment> selectedStoryComments){
        currentStory = selectedStory;
        currentStoryComments.clear();
        currentStoryComments.addAll(selectedStoryComments);
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
}
