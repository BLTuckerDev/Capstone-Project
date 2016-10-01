package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.support.annotation.VisibleForTesting;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    public Observable<Comment> get(final long[] commentIds) {
        return downloadComments(commentIds)
                .filter(new Func1<CommentDto, Boolean>() {
                    @Override
                    public Boolean call(CommentDto commentDto) {
                        return commentDto.text != null && commentDto.text.trim().length() > 0;
                    }
                })
                .map(new Func1<CommentDto, Comment>() {
                    @Override
                    public Comment call(CommentDto commentDto) {
                        return new Comment(commentDto.id, commentDto.by, commentDto.text, commentDto.time, commentDto.parent);
                    }
                });
    }

    private Observable<CommentDto> downloadComments(final long[] commentIds) {
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
                    .concatMap(new Func1<CommentDto, Observable<CommentDto>>() {
                        @Override
                        public Observable<CommentDto> call(CommentDto commentDto) {
                            return Observable.just(commentDto).mergeWith(downloadComments(commentDto.kids));
                        }
                    });
        }
    }

}
