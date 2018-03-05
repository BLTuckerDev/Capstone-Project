package dev.bltucker.nanodegreecapstone.common.sync

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import dev.bltucker.nanodegreecapstone.common.injection.DaggerInjector
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.IO
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class OrphanCommentDeleteJob : JobService() {

    @field:[Inject IO]
    lateinit var ioScheduler: Scheduler

    @Inject
    lateinit var deleteOrphanCommentsAction: DeleteOrphanCommentsAction

    override fun onCreate() {
        DaggerInjector.getApplicationComponent().inject(this)
        super.onCreate()
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {

        Completable.fromAction(deleteOrphanCommentsAction)
                .subscribeOn(ioScheduler)
                .subscribe(object : CompletableObserver{
                    override fun onComplete() {
                        jobFinished(jobParameters, false)
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e, "Error attempting to complete comment clean up")
                        jobFinished(jobParameters, false)
                    }
                })

        //the boolean we return from here needs to answer the question "Is there still work going on?"
        return true
    }

    override fun onStartJob(job: JobParameters?): Boolean {
        //the boolean we return from here needs to answer the question "Should this job be retried"
        return false
    }

    companion object {
        @JvmField
        val JOB_TAG = "dev.bltucker.nanodegreecapstone.common.sync.OrphanCommentDeleteJob"
    }
}