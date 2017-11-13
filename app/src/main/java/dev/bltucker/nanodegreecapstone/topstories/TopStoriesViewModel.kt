package dev.bltucker.nanodegreecapstone.topstories

import android.arch.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope
import dev.bltucker.nanodegreecapstone.data.StoryRepository
import dev.bltucker.nanodegreecapstone.models.Story
import io.reactivex.Observable
import javax.inject.Inject

@ApplicationScope
class TopStoriesViewModel @Inject constructor(private val storyRepository: StoryRepository,
                                              private val syncRequestDelegate: SyncRequestDelegate) : ViewModel(){

    private val commentButtonClickPublisher : PublishRelay<Story> = PublishRelay.create()

    private val readLaterStoryClickPublisher : PublishRelay<Story> = PublishRelay.create()

    fun onCommentsButtonClick(story : Story){
        commentButtonClickPublisher.accept(story)
    }

    fun onReadLaterStoryButtonClick(story : Story){
        readLaterStoryClickPublisher.accept(story)
    }

    fun onRefreshTopStories(){
        syncRequestDelegate.sendSyncRequest()
    }

    fun onShowRefreshedTopStories(){
        //TODO need to flip some kind of flag that says update the storylist observable
    }


    fun getObservableStories(): Observable<List<Story>> {
        //TODO need to only allow the observable to emit only if the user has said to refresh
        return storyRepository.allStories
    }

    fun getObservableCommentClicks() : Observable<Story>{
        return commentButtonClickPublisher
    }

    fun getObservableReadLaterStoryClicks() : Observable<Story>{
        return readLaterStoryClickPublisher
    }

}