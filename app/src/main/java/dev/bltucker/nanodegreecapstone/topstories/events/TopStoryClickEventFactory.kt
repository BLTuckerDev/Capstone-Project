package dev.bltucker.nanodegreecapstone.topstories.events

import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope
import dev.bltucker.nanodegreecapstone.models.Story
import javax.inject.Inject

@ApplicationScope
class TopStoryClickEventFactory @Inject constructor() {

    fun createOnCommentsButtonClick(story: Story): CommentsButtonClickEvent {
        return CommentsButtonClickEvent(story)
    }

    fun createReadLaterButtonClickEvent(story: Story): ReadLaterButtonClickEvent {
        return ReadLaterButtonClickEvent(story)
    }
}
