package dev.bltucker.nanodegreecapstone.topstories

import android.content.Intent
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import dev.bltucker.nanodegreecapstone.common.injection.DaggerInjector
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService
import dev.bltucker.nanodegreecapstone.data.StoryRepository
import dev.bltucker.nanodegreecapstone.models.Story
import dev.bltucker.nanodegreecapstone.widget.TopFiveStoriesWidgetProvider
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class TopStoriesUpdateService : JobService() {

    companion object {
        const val JOB_TAG = "dev.bltucker.nanodegreecapstone.topstories.TopStoriesUpdateService"
    }

    @field:[Inject]
    lateinit var hackerNewsApi: HackerNewsApiService

    @field:[Inject]
    lateinit var storyIdToStoryTransformer: StoryIdToStoryTransformer

    @field:[Inject]
    lateinit var storyRepository : StoryRepository

    override fun onCreate() {
        DaggerInjector.getApplicationComponent().inject(this)
        super.onCreate()
    }

    override fun onStopJob(job: JobParameters): Boolean {
        return false //Answers the question "Should this job be retried"
    }

    override fun onStartJob(job: JobParameters): Boolean {

        hackerNewsApi.topStoryIds
                .compose(storyIdToStoryTransformer)
                .subscribe(object : SingleObserver<List<Story>> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(t: List<Story>) {
                        storyRepository.saveStories(t.toTypedArray())
                        notifyWidgets()
                        jobFinished(job, false)
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e, "Error downloading top stories")
                        jobFinished(job, false)
                    }
                })

        return true //Answers the question "Is work still going on"
    }

    private fun notifyWidgets() {
        val syncCompletedIntent = Intent()
        syncCompletedIntent.`package` = applicationContext.packageName
        syncCompletedIntent.action = TopFiveStoriesWidgetProvider.SYNC_COMPLETED_ACTION
        applicationContext.sendBroadcast(syncCompletedIntent)
    }

}
