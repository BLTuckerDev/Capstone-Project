package dev.bltucker.nanodegreecapstone.storydetail

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import dev.bltucker.nanodegreecapstone.data.daos.ReadLaterStoryDao
import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.IO
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.UI
import dev.bltucker.nanodegreecapstone.models.Comment
import dev.bltucker.nanodegreecapstone.models.ReadLaterStory
import dev.bltucker.nanodegreecapstone.models.Story
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@ApplicationScope
class StoryDetailViewModel @Inject constructor(private val readLaterStoryDao: ReadLaterStoryDao,
                                               private val commentsRepository: CommentRepository,
                                               @param:UI private val uiScheduler: Scheduler,
                                               @param:IO private val ioscheduler: Scheduler) : ViewModel() {

    val readLaterSaveSuccessPublisher: PublishRelay<Boolean> = PublishRelay.create()

    fun onSaveStoryClick(story: Story) {

        val readLaterStory = ReadLaterStory(story.id, story.posterName, story.title, story.url)
        Completable.fromAction({
            readLaterStoryDao.saveStory(readLaterStory)
        })
                .subscribeOn(ioscheduler)
                .observeOn(uiScheduler)
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        readLaterSaveSuccessPublisher.accept(true)
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        Timber.e(e, "Error while attempting to save a read later story.")
                    }
                })
    }

    fun getObservableReadLaterSuccessStatus() : Observable<Boolean>{
        return readLaterSaveSuccessPublisher
    }

    fun getObservableComments(storyId: Long) : Observable<Array<Comment>>{
        return commentsRepository.getCommentsForStoryId(storyId)
    }
}