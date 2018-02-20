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
import dev.bltucker.nanodegreecapstone.common.data.HackerNewsApiService
import dev.bltucker.nanodegreecapstone.common.data.StoryRepository
import dev.bltucker.nanodegreecapstone.common.models.Story
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

    private val modelPublisher: BehaviorRelay<TopStoryModel> = BehaviorRelay.createDefault(topStoryModelFactory.createLoadingModel(listOf()))

    private val clickEventPublisher: PublishRelay<TopStoryClickEvent> = PublishRelay.create()

    private val viewModelDisposables: CompositeDisposable = CompositeDisposable()

    init {
        storyRepository.allStories
                .filter{ stories -> stories.isNotEmpty() }
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe(object : Observer<List<Story>> {

                    override fun onError(e: Throwable) {
                        val previousModel = modelPublisher.value
                        modelPublisher.accept(topStoryModelFactory.createErrorModel(previousModel.storyList, previousModel.refreshedStoryList))
                    }

                    override fun onSubscribe(d: Disposable) {
                        viewModelDisposables.add(d)
                    }

                    override fun onComplete() {}

                    override fun onNext(latestStories: List<Story>) {
                        val lastModel = modelPublisher.value

                        //if we had no stories showing, then just show these freshly loaded ones.
                        if (lastModel.storyList.isEmpty()) {
                            modelPublisher.accept(topStoryModelFactory.createTopStoryModelWithStories(latestStories))
                            return
                        }

                        //if we were not refreshing then we want to publish here
                        if (!lastModel.isRefreshing) {
                            modelPublisher.accept(topStoryModelFactory.createTopStoryModelWithStories(latestStories))
                        } else if (lastModel.isRefreshing) {
                            //we were refreshing so let the user know there are fresh stories to view
                            modelPublisher.accept(topStoryModelFactory.createTopStoryModelWithRefreshedStories(lastModel.storyList, latestStories))
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
        modelPublisher.accept(topStoryModelFactory.createLoadingModel(modelPublisher.value.storyList))
        fetchData()
    }

    fun onRefreshTopStories() {
        val previousModel = modelPublisher.value
        modelPublisher.accept(topStoryModelFactory.createRefreshingModel(previousModel.storyList, previousModel.refreshedStoryList))
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
                        val previousModel = modelPublisher.value
                        modelPublisher.accept(topStoryModelFactory.createErrorModel(previousModel.storyList, previousModel.refreshedStoryList))
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