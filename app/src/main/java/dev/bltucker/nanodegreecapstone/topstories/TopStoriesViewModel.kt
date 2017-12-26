package dev.bltucker.nanodegreecapstone.topstories

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.IO
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.UI
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService
import dev.bltucker.nanodegreecapstone.data.StoryRepository
import dev.bltucker.nanodegreecapstone.models.Story
import dev.bltucker.nanodegreecapstone.topstories.events.TopStoryClickEvent
import dev.bltucker.nanodegreecapstone.topstories.events.TopStoryClickEventFactory
import dev.bltucker.nanodegreecapstone.widget.TopFiveStoriesWidgetProvider.SYNC_COMPLETED_ACTION
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

@ApplicationScope
class TopStoriesViewModel @Inject constructor(private val storyRepository: StoryRepository,
                                              private val applicationContext: Context,
                                              private val storyIdToStoryTransformer: StoryIdToStoryTransformer,
                                              private val apiService: HackerNewsApiService,
                                              private val topStoryModelFactory: TopStoryModelFactory,
                                              @IO private val ioScheduler: Scheduler,
                                              @UI private val uiScheduler: Scheduler,
                                              private val clickEventFactory: TopStoryClickEventFactory) : ViewModel() {

    private val modelPublisher: BehaviorRelay<TopStoryModel> = BehaviorRelay.createDefault(topStoryModelFactory.createLoadingModel(null))

    private val clickEventPublisher: PublishRelay<TopStoryClickEvent> = PublishRelay.create()

    private val viewModelDisposables: CompositeDisposable = CompositeDisposable()

    init {
        storyRepository.allStories
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe(object : Observer<List<Story>> {

                    override fun onError(e: Throwable) {
                        modelPublisher.accept(topStoryModelFactory.createErrorModel(modelPublisher.value))
                    }

                    override fun onSubscribe(d: Disposable) {
                        viewModelDisposables.add(d)
                    }

                    override fun onComplete() {}

                    override fun onNext(latestStories: List<Story>) {
                        val lastModel = modelPublisher.value

                        //if the last thing was null then lets just the stories
                        if (lastModel == null) {
                            modelPublisher.accept(topStoryModelFactory.createTopStoryModelWithStories(latestStories))
                            return
                        }

                        //if we were loading and have no stories we want to wait for stories to come in so don't publish
                        //because either we will get stories or an error will be thrown
                        if (lastModel.isLoading && latestStories.isEmpty()) {
                            return
                        }

                        //if we were not refreshing then we want to publish here
                        if (!lastModel.isRefreshing) {
                            modelPublisher.accept(topStoryModelFactory.createTopStoryModelWithStories(latestStories))
                        } else if (lastModel.isRefreshing) {
                            modelPublisher.accept(topStoryModelFactory.createTopStoryModelWithRefreshedStories(lastModel, latestStories))
                        }

                    }
                })
    }

    fun getObservableModelEvents(): Observable<TopStoryModel> {
        return modelPublisher
    }

    fun getObservableClickEvents(): Observable<TopStoryClickEvent> {
        return clickEventPublisher
    }

    fun onCommentsButtonClick(story: Story) {
        clickEventPublisher.accept(clickEventFactory.createOnCommentsButtonClick(story))
    }

    fun onReadLaterStoryButtonClick(story: Story) {
        clickEventPublisher.accept(clickEventFactory.createReadLaterButtonClickEvent(story))
    }

    fun onLoadTopStories() {
        modelPublisher.accept(topStoryModelFactory.createLoadingModel(modelPublisher.value))
        fetchData()
    }

    fun onRefreshTopStories() {
        modelPublisher.accept(topStoryModelFactory.createRefreshingModel(modelPublisher.value))
        fetchData()
    }

    private fun fetchData() {
        apiService.topStoryIds
                .compose(storyIdToStoryTransformer)
                .subscribeOn(ioScheduler)
                .subscribe(object : SingleObserver<List<Story>> {

                    var startTime : Long = 0

                    override fun onSubscribe(d: Disposable) {
                        startTime = System.currentTimeMillis()
                    }

                    override fun onSuccess(t: List<Story>) {
                        Log.d("StoryFetch", "Fetched stories in ${System.currentTimeMillis() - startTime} milliseconds")
                        storyRepository.saveStories(t.toTypedArray())
                        notifyWidgets()
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e, "Error downloading top stories")
                        modelPublisher.accept(topStoryModelFactory.createErrorModel(modelPublisher.value))
                    }
                })
    }

    fun onShowRefreshedTopStories() {
        val lastModel = modelPublisher.value
        if (lastModel != null) {
            modelPublisher.accept(topStoryModelFactory.createTopStoryModelWithStories(lastModel.refreshedStoryList))
        }
    }

    override fun onCleared() {
        viewModelDisposables.clear()
    }

    private fun notifyWidgets() {
        val syncCompletedIntent = Intent()
        syncCompletedIntent.`package` = applicationContext.packageName
        syncCompletedIntent.action = SYNC_COMPLETED_ACTION
        applicationContext.sendBroadcast(syncCompletedIntent)
    }
}