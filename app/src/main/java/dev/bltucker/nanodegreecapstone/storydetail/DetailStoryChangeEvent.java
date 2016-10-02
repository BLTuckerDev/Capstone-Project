package dev.bltucker.nanodegreecapstone.storydetail;

import java.util.List;

import dev.bltucker.nanodegreecapstone.models.Comment;

public class DetailStoryChangeEvent {
    final List<Comment> oldComments;
    final List<Comment> commentList;

    DetailStoryChangeEvent(List<Comment> oldComments, List<Comment> commentList) {
        this.oldComments = oldComments;
        this.commentList = commentList;
    }
}
