package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.support.annotation.VisibleForTesting;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.models.Comment;
import rx.Observable;
import rx.functions.Func1;

@ApplicationScope
public final class StoryCommentsObservableFactory {

    @VisibleForTesting
    final HackerNewsApiService hackerNewsApiService;

    @Inject
    public StoryCommentsObservableFactory(HackerNewsApiService hackerNewsApiService) {
        this.hackerNewsApiService = hackerNewsApiService;
    }

    public Observable<Comment> get(final long storyId, final long[] commentIds) {
        return downloadComments(storyId, commentIds, 0);
    }

    private Observable<Comment> downloadComments(final long storyId, final long[] commentIds, final int commentDepth) {
        if (commentIds == null || commentIds.length == 0) {
            return Observable.empty();
        } else {
            return Observable.range(0, commentIds.length)
                    .concatMap(new Func1<Integer, Observable<CommentDto>>() {
                        @Override
                        public Observable<CommentDto> call(Integer index) {
                            return hackerNewsApiService.getComment(commentIds[index]);
                        }
                    })
                    .concatMap(new Func1<CommentDto, Observable<Comment>>() {
                        @Override
                        public Observable<Comment> call(CommentDto commentDto) {
                            final int childDepth = commentDepth + 1;
                            return Observable.just(new Comment(storyId, commentDto.id, commentDto.by, commentDto.text, commentDto.time, commentDto.parent, commentDepth))
                                    .concatWith(downloadComments(storyId, commentDto.kids, childDepth));
                        }
                    })
                    .filter(new Func1<Comment, Boolean>() {
                        @Override
                        public Boolean call(Comment comment) {
                            return comment.getCommentText().trim().length() > 0;
                        }
                    });
        }
    }

}
