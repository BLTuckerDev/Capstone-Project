package dev.bltucker.nanodegreecapstone.storydetail

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import dev.bltucker.nanodegreecapstone.R
import dev.bltucker.nanodegreecapstone.models.Story
import dev.bltucker.nanodegreecapstone.settings.SettingsActivity
import dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentModule
import timber.log.Timber

class StoryDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_detail)
        if (null == savedInstanceState) {
            addStoryDetailFragment(intent.extras)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_story_detail_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menu_item_settings) {
            SettingsActivity.launch(this)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun addStoryDetailFragment(bundle: Bundle?) {
        if (null == bundle) {
            Timber.e("StoryDetailActivity launched without a bundle!")
            return
        }

        if (!bundle.containsKey(StoryDetailFragmentModule.STORY_BUNDLE_KEY)) {
            Timber.e("StoryDetailActivity bundle is missing the story position")
            return
        }

        val story = bundle.getParcelable<Story>(StoryDetailFragmentModule.STORY_BUNDLE_KEY)
        val storyDetailFragment = StoryDetailFragment.newInstance(story)

        supportFragmentManager.beginTransaction()
                .add(R.id.story_activity_coordinator_layout, storyDetailFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
    }

    companion object {

        fun launch(activity: Activity, story: Story?) {
            val launchIntent = Intent(activity, StoryDetailActivity::class.java)
            launchIntent.putExtra(StoryDetailFragmentModule.STORY_BUNDLE_KEY, story)
            ActivityCompat.startActivity(activity, launchIntent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }
    }
}
