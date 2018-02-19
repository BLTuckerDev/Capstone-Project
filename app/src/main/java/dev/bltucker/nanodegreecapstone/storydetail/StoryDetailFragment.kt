package dev.bltucker.nanodegreecapstone.storydetail

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.ShareActionProvider
import android.view.*
import android.widget.Toast
import dev.bltucker.nanodegreecapstone.R
import dev.bltucker.nanodegreecapstone.common.ApplicationViewModelsFactory
import dev.bltucker.nanodegreecapstone.databinding.FragmentStoryDetailBinding
import dev.bltucker.nanodegreecapstone.common.injection.DaggerInjector
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.UI
import dev.bltucker.nanodegreecapstone.common.models.Comment
import dev.bltucker.nanodegreecapstone.common.models.Story
import dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentModule
import dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentModule.Companion.DETAIL_STORY_BUNDLE_KEY
import dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentModule.Companion.STORY_BUNDLE_KEY

import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class StoryDetailFragment : Fragment() {

    @Inject
    lateinit var commentsAdapter: StoryCommentsAdapter

    @Inject
    lateinit var storyCommentsSyncer: StoryCommentsSyncer

    @Inject
    lateinit var applicationViewModelsFactory: ApplicationViewModelsFactory

    @field:[Inject UI]
    lateinit var uiScheduler: Scheduler

    @Inject
    @JvmField
    var story: Story? = null

    internal var shareActionProvider: ShareActionProvider? = null

    internal lateinit var shareMenuItem: MenuItem

    private lateinit var binding: FragmentStoryDetailBinding

    private lateinit var storyDetailViewModel: StoryDetailViewModel

    private val modelSubscriptions = CompositeDisposable()

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_detail_fragment, menu)

        shareMenuItem = menu!!.findItem(R.id.menu_item_share_story)
        shareMenuItem.isVisible = false
        shareActionProvider = MenuItemCompat.getActionProvider(shareMenuItem) as ShareActionProvider

        val saveItem = menu.findItem(R.id.menu_item_save_story)
        DrawableCompat.setTint(saveItem.icon, Color.WHITE)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        if (shareActionProvider != null && story != null) {
            setupShareActionProvider()
            shareMenuItem.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.menu_item_save_story) {
            if (story != null) {
                storyDetailViewModel.onSaveStoryClick(story!!)
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupShareActionProvider() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, story!!.getTitle())
        shareIntent.putExtra(Intent.EXTRA_TEXT, story!!.getUrl())
        shareActionProvider!!.setShareIntent(shareIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        DaggerInjector.getApplicationComponent()
                .storyDetailComponent(StoryDetailFragmentModule(this, savedInstanceState))
                .inject(this)
        storyDetailViewModel = ViewModelProviders.of(this, applicationViewModelsFactory).get(StoryDetailViewModel::class.java)
        lifecycle.addObserver(storyCommentsSyncer)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable(DETAIL_STORY_BUNDLE_KEY, story)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStoryDetailBinding.inflate(inflater!!, container, false)
        binding.headerInclude?.readButton?.setOnClickListener { _ -> showStoryPostUrl() }
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.commentListRecyclerview.layoutManager = LinearLayoutManager(view!!.context)
        binding.commentListRecyclerview.adapter = commentsAdapter
    }

    override fun onStart() {
        super.onStart()

        if (story != null) {
            showStory()

            storyDetailViewModel.getObservableComments(story!!.getId())
                    .observeOn(uiScheduler)
                    .subscribe(object : Observer<Array<Comment>> {
                        override fun onSubscribe(d: Disposable) {
                            modelSubscriptions.add(d)
                        }

                        override fun onNext(comments: Array<Comment>) {
                            commentsAdapter.updateComments(comments)
                        }

                        override fun onError(e: Throwable) {
                            showGenericError("Error loading story comments")
                        }

                        override fun onComplete() {}
                    })

            storyDetailViewModel.getObservableReadLaterSuccessStatus()
                    .observeOn(uiScheduler)
                    .subscribe(object : Observer<Boolean> {
                        override fun onNext(saveSuccess: Boolean) {
                            if (saveSuccess) {
                                Toast.makeText(context, getString(R.string.story_saved), Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onSubscribe(d: Disposable) {
                            modelSubscriptions.add(d)
                        }

                        override fun onError(e: Throwable) {}

                        override fun onComplete() {}
                    })
        } else {
            showEmptyView()
        }

    }

    private fun showGenericError(errorMessage: String) {
        Snackbar.make(binding.coordinatorLayout, errorMessage, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun onStop() {
        modelSubscriptions.clear()
        super.onStop()
    }

    fun showStory() {
        if (activity != null) {
            activity.invalidateOptionsMenu()
        }

        if (null == story) {
            return
        }

        binding.headerInclude?.storyTitleTextview?.text = story!!.getTitle()
        binding.headerInclude?.storyUrlTextview?.text = story!!.getUrl()
        binding.headerInclude?.posterNameTextview?.text = String.format(getString(R.string.by_poster), story!!.getPosterName())
        binding.headerInclude?.scoreTextview?.text = String.format(getString(R.string.story_score), story!!.getScore())
        if (null == story!!.getUrl()) {
            binding.headerInclude?.readButton?.visibility = View.INVISIBLE
        } else {
            binding.headerInclude?.readButton?.visibility = View.VISIBLE
        }
    }

    fun showStoryPostUrl() {
        if (story != null && story!!.getUrl() != null) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(story!!.getUrl()))
            startActivity(browserIntent)
        }
    }

    fun showEmptyView() {
        binding.commentListRecyclerview.visibility = View.INVISIBLE
        binding.emptyViewContainer.visibility = View.VISIBLE
        binding.appBarLayout.visibility = View.INVISIBLE
        binding.headerInclude?.detailHeaderCardview?.visibility = View.INVISIBLE
    }

    companion object {

        fun newInstance(selectedStory: Story?): StoryDetailFragment {
            val storyDetailFragment = StoryDetailFragment()
            val args = Bundle()
            args.putParcelable(STORY_BUNDLE_KEY, selectedStory)
            storyDetailFragment.arguments = args
            return storyDetailFragment
        }
    }
}
