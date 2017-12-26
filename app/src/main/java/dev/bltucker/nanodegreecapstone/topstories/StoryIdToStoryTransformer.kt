package dev.bltucker.nanodegreecapstone.topstories

import com.github.dmstocking.optional.java.util.Optional
import dev.bltucker.nanodegreecapstone.common.injection.ApplicationScope
import dev.bltucker.nanodegreecapstone.common.injection.qualifiers.StoryMax
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService
import dev.bltucker.nanodegreecapstone.models.Story
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import javax.inject.Inject


@ApplicationScope
class StoryIdToStoryTransformer @Inject constructor(private val hackerNewsApiService: HackerNewsApiService,
                                                    @StoryMax private val storyMax: Int) : SingleTransformer<Array<Long>, List<Story>>{

    override fun apply(upstream: Single<Array<Long>>): SingleSource<List<Story>> {
        return upstream
                .toObservable()
                .filter { storyIds: Array<Long> -> storyIds.isNotEmpty() }
                .concatMap({ storyIds : Array<Long> -> Observable.fromArray(*storyIds) })
                .take(storyMax.toLong())
                .concatMap({ storyId: Long -> hackerNewsApiService.getStory(storyId).toObservable() })
                .scan(Optional.empty(), { previousStory: Optional<Story>, currentStory: Story ->
                    if (previousStory.isPresent) {
                        currentStory.setStoryRank(previousStory.get().storyRank + 1)
                    } else {
                        currentStory.setStoryRank(0)
                    }

                    Optional.of(currentStory)
                })
                .filter({ optionalStory -> optionalStory.isPresent})
                .map { optionalStory: Optional<Story> -> optionalStory.get() }
                .toList()
    }
}
