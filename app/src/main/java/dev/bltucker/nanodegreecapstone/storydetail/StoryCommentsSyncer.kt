package dev.bltucker.nanodegreecapstone.storydetail

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import dev.bltucker.nanodegreecapstone.common.data.HackerNewsApiService
import dev.bltucker.nanodegreecapstone.common.models.Comment
import dev.bltucker.nanodegreecapstone.common.models.Story
import dev.bltucker.nanodegreecapstone.storydetail.data.CommentDto
import dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentScope
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@StoryDetailFragmentScope
class StoryCommentsSyncer @Inject constructor(private val hackerNewsApiService: HackerNewsApiService,
                                              private val commentRepository: CommentRepository,
                                              private val story: Story?) : LifecycleObserver {

    lateinit var commentLoaderSubscription: Disposable

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        if (story == null) {
            return
        }

        hackerNewsApiService.getStory(story.id)
                .flatMapObservable { story -> Observable.just(story.commentIds) }
                .concatMap { topLevelCommentIds ->
                    val commentIds = LongArray(topLevelCommentIds.size)
                    for (i in commentIds.indices) {
                        commentIds[i] = topLevelCommentIds[i]
                    }
                    downloadComments(story.id, commentIds, 0)
                }
                .filter { comment -> !comment.getCommentText().isNullOrBlank() }
                .subscribe(object : Observer<Comment> {
                    override fun onSubscribe(d: Disposable) {
                        commentLoaderSubscription = d
                    }

                    override fun onNext(comment: Comment) {
                        commentRepository.saveComment(comment)
                    }

                    override fun onError(e: Throwable) {}

                    override fun onComplete() {}
                })


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        if (story == null) {
            return
        }

        if (!commentLoaderSubscription.isDisposed) {
            commentLoaderSubscription.dispose()
        }
    }


    private fun downloadComments(storyId: Long, commentIds: LongArray?, commentDepth: Int): Observable<Comment> {
        return if (commentIds == null || commentIds.isEmpty()) {
            Observable.empty()
        } else {
            Observable.range(0, commentIds.size)
                    .concatMap<CommentDto> { index -> hackerNewsApiService.getComment(commentIds[index]).toObservable() }
                    .concatMap { commentDto ->
                        val childDepth = commentDepth + 1
                        Observable.just(Comment(commentDto.id, storyId, commentDto.by, commentDto.text, commentDto.time, commentDto.parent, commentDepth))
                                .concatWith(downloadComments(storyId, commentDto.kids, childDepth))
                    }
                    .filter { comment -> !comment.getCommentText().isNullOrBlank() }
        }
    }
}
