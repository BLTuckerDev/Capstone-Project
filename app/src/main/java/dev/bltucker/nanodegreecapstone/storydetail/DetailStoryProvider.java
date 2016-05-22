package dev.bltucker.nanodegreecapstone.storydetail;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;

class DetailStoryProvider {

    public DetailStory getDetailStory(Story aStory, List<Comment> commentList){
        DetailStory detailStory = new DetailStory(aStory);
        detailStory.setComments(commentList);
        return detailStory;
    }
}
