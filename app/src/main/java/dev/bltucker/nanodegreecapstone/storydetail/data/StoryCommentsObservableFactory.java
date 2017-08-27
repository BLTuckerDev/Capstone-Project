package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.support.annotation.VisibleForTesting;

import javax.inject.Inject;

import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.models.Comment;
import io.reactivex.Observable;

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
                    .concatMap(index -> hackerNewsApiService.getComment(commentIds[index]).toObservable())
                    .concatMap(commentDto -> {
                        final int childDepth = commentDepth + 1;
                        return Observable.just(new Comment(commentDto.id, storyId, commentDto.by, commentDto.text, commentDto.time, commentDto.parent, commentDepth))
                                .concatWith(downloadComments(storyId, commentDto.kids, childDepth));
                    })
                    .filter(comment -> comment.getCommentText().trim().length() > 0);
        }
    }

}
