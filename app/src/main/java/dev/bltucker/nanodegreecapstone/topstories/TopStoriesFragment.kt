package dev.bltucker.nanodegreecapstone.topstories

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.bltucker.nanodegreecapstone.R
import dev.bltucker.nanodegreecapstone.common.ApplicationViewModelsFactory
import dev.bltucker.nanodegreecapstone.common.injection.DaggerInjector
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.UI
import dev.bltucker.nanodegreecapstone.databinding.FragmentStoryListBinding
import dev.bltucker.nanodegreecapstone.models.Story
import dev.bltucker.nanodegreecapstone.topstories.events.TopStoryClickEvent
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class TopStoriesFragment : Fragment() {

    @Inject
    lateinit var adapter: StoryListAdapter

    @Inject
    lateinit var applicationViewModelsFactory: ApplicationViewModelsFactory

    @field:[Inject UI]
    lateinit var uiScheduler: Scheduler

    private val clickEventDisposable = CompositeDisposable()

    private var modelDisposable: Disposable? = null

    private lateinit var topStoriesViewModel: TopStoriesViewModel

    private var delegate: Delegate? = null

    private lateinit var binding: FragmentStoryListBinding

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Delegate) {
            this.delegate = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerInjector.getApplicationComponent().inject(this)
        topStoriesViewModel = ViewModelProviders.of(this, applicationViewModelsFactory).get(TopStoriesViewModel::class.java)
        topStoriesViewModel.onLoadTopStories()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStoryListBinding.inflate(inflater, container, false)
        binding.swipeToRefreshLayout.setOnRefreshListener { topStoriesViewModel.onRefreshTopStories() }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.storyListRecyclerview.layoutManager = LinearLayoutManager(context)
        binding.storyListRecyclerview.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        observeModelChanges()
    }

    override fun onResume() {
        super.onResume()
        observeClickEvents()
    }

    override fun onPause() {
        clickEventDisposable.clear()
        super.onPause()
    }

    override fun onStop() {
        if (modelDisposable != null) {
            modelDisposable!!.dispose()
        }
        super.onStop()
    }

    override fun onDetach() {
        this.delegate = null
        super.onDetach()
    }

    fun showStoryDetailView(story: Story) {
        if (delegate != null) {
            delegate!!.showCommentsView(story)
        }
    }

    fun showStoryPostUrl(url: String) {
        val activity = this.activity ?: return

        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ResourcesCompat.getColor(activity.resources, R.color.colorPrimary, activity.theme))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(activity, Uri.parse(url))
    }

    private fun showUpdatedStoriesNotification() {
        if (null == view) {
            return
        }

        binding.swipeToRefreshLayout.isRefreshing = false

        Snackbar.make(view!!, R.string.new_stories_are_available, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.refresh) { v -> topStoriesViewModel.onShowRefreshedTopStories() }
                .show()
    }

    private fun observeModelChanges() {
        topStoriesViewModel.getObservableModelEvents()
                .observeOn(uiScheduler)
                .subscribe(object : Observer<TopStoryModel> {
                    override fun onSubscribe(d: Disposable) {
                        modelDisposable = d
                    }

                    override fun onNext(topStoryModel: TopStoryModel) {
                        binding.topStoryModel = topStoryModel
                        binding.executePendingBindings()

                        when {
                            topStoryModel.isError -> renderErrorModel()
                            !topStoryModel.refreshedStoryList.isEmpty() -> renderRefreshedStoriesModel()
                            else -> renderTopStoryModel(topStoryModel)
                        }
                    }

                    override fun onError(e: Throwable) {}

                    override fun onComplete() {}
                })
    }

    private fun renderTopStoryModel(topStoryModel: TopStoryModel) {
        adapter.updateStories(topStoryModel.storyList)
    }

    private fun renderRefreshedStoriesModel() {
        binding.swipeToRefreshLayout.isRefreshing = false
        showUpdatedStoriesNotification()
    }

    private fun renderErrorModel() {
        showErrorSnackbar()
    }

    private fun showErrorSnackbar() {
        val view = view ?: return

        Snackbar.make(view, R.string.error_loading_stories, Snackbar.LENGTH_INDEFINITE).show()
    }

    private fun observeClickEvents() {
        topStoriesViewModel.getObservableClickEvents()
                .observeOn(uiScheduler)
                .subscribe(object : Observer<TopStoryClickEvent> {
                    override fun onSubscribe(d: Disposable) {
                        clickEventDisposable.add(d)
                    }

                    override fun onNext(topStoryClickEvent: TopStoryClickEvent) {
                        topStoryClickEvent.execute(this@TopStoriesFragment)
                    }

                    override fun onError(e: Throwable) {}

                    override fun onComplete() {}
                })
    }

    interface Delegate {
        fun showCommentsView(story: Story)
    }
}
