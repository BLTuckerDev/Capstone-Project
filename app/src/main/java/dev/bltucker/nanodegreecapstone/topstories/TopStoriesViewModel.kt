package dev.bltucker.nanodegreecapstone.topstories

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.Intent
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.IO
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.UI
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService
import dev.bltucker.nanodegreecapstone.data.StoryRepository
import dev.bltucker.nanodegreecapstone.models.Story
import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter.SYNC_COMPLETED_ACTION
import dev.bltucker.nanodegreecapstone.topstories.events.TopStoryClickEvent
import dev.bltucker.nanodegreecapstone.topstories.events.TopStoryClickEventFactory
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

@ApplicationScope
class TopStoriesViewModel @Inject constructor(private val storyRepository: StoryRepository,
                                              private val applicationContext: Context,
                                              private val apiService: HackerNewsApiService,
                                              private val topStoryModelFactory: TopStoryModelFactory,
                                              @IO private val ioScheduler: Scheduler,
                                              @UI private val uiScheduler: Scheduler,
                                              private val clickEventFactory: TopStoryClickEventFactory) : ViewModel() {

    private val modelPublisher : BehaviorRelay<TopStoryModel> = BehaviorRelay.createDefault(topStoryModelFactory.createLoadingModel())

    private val clickEventPublisher: PublishRelay<TopStoryClickEvent> = PublishRelay.create()

    private val refreshEventPublisher: PublishRelay<Object> = PublishRelay.create()

    private val viewModelDisposables: CompositeDisposable = CompositeDisposable()

    private var refreshedModel: TopStoryModel? = null

    init {
        storyRepository.allStories.subscribeOn(ioScheduler).observeOn(uiScheduler)
                .subscribe(object : Observer<List<Story>> {

                    var hasLoadedStories : Boolean = false

                    override fun onError(e: Throwable) {}

                    override fun onSubscribe(d: Disposable) {
                        viewModelDisposables.add(d)
                    }

                    override fun onComplete() {}

                    override fun onNext(latestStories: List<Story>) {
                        if(!hasLoadedStories){
                            modelPublisher.accept(topStoryModelFactory.createTopStoryModelWithStories(latestStories))
                            hasLoadedStories = true
                        } else {
                            refreshedModel = topStoryModelFactory.createTopStoryModelWithStories(latestStories)
                            refreshEventPublisher.accept(Object())
                        }
                    }
                })
    }

    fun getObservableModelEvents() : Observable<TopStoryModel>{
        return modelPublisher
    }

    fun getObservableClickEvents(): Observable<TopStoryClickEvent> {
        return clickEventPublisher
    }

    fun getObservableRefreshEvents(): Observable<Object>{
        return refreshEventPublisher
    }

    fun onCommentsButtonClick(story: Story) {
        clickEventPublisher.accept(clickEventFactory.createOnCommentsButtonClick(story))
    }

    fun onReadLaterStoryButtonClick(story: Story) {
        clickEventPublisher.accept(clickEventFactory.createReadLaterButtonClickEvent(story))
    }

    fun onRefreshTopStories() {
        apiService.topStoryIds
                .toObservable()
                .concatMap { storyIdList ->
                    val storyArrayLength = Math.min(storyIdList.size, 100)
                    val stories = mutableListOf<Story>()

                    for (i in 0 until storyArrayLength) {
                        val story = apiService.getStory(storyIdList[i]).blockingGet()
                        story.setStoryRank(i)
                        stories.add(story)
                    }

                    Observable.just<Array<Story>>(stories.toTypedArray())
                }
                .subscribeOn(ioScheduler)
                .subscribe(object : Observer<Array<Story>>{
                    override fun onSubscribe(d: Disposable) { }

                    override fun onComplete() { }

                    override fun onNext(t: Array<Story>) {
                        storyRepository.saveStories(t)
                        notifyWidgets()
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e, "Error downloading top stories")
                    }
                })
    }

    fun onShowRefreshedTopStories() {
        if(refreshedModel != null){
            modelPublisher.accept(refreshedModel)
        }
    }

    override fun onCleared() {
        viewModelDisposables.clear()
    }

    private fun notifyWidgets() {
        val syncCompletedIntent = Intent()
        syncCompletedIntent.`package` = applicationContext.getPackageName()
        syncCompletedIntent.action = SYNC_COMPLETED_ACTION
        applicationContext.sendBroadcast(syncCompletedIntent)
    }
}