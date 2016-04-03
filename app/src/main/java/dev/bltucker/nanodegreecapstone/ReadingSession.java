package dev.bltucker.nanodegreecapstone;

import java.util.List;

import dev.bltucker.nanodegreecapstone.data.Comment;
import dev.bltucker.nanodegreecapstone.data.Story;

public final class ReadingSession {

    private Story currentStory;

    private List<Comment> currentStoryComments;

    private List<Story> storyList;



    public void read(Story selectedStory, List<Comment> selectedStoryComments){
        currentStory = selectedStory;
        currentStoryComments.clear();
        currentStoryComments.addAll(selectedStoryComments);
    }

    public void addStories(List<Story> stories){
        storyList.addAll(stories);
    }
}
