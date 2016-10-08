package dev.bltucker.nanodegreecapstone.storydetail.events;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;

public class DetailStoryChangeEvent {
    public final List<Comment> oldComments;
    public final List<Comment> commentList;

    public DetailStoryChangeEvent(List<Comment> oldComments, List<Comment> commentList) {
        this.oldComments = oldComments;
        this.commentList = commentList;
    }
}
