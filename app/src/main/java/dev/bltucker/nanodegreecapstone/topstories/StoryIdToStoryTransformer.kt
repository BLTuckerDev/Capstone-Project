package dev.bltucker.nanodegreecapstone.topstories

import android.util.Log
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
                .scan(Pair<Int, Long>(-1,0), {previousPair, storyId ->
                    return@scan Pair(previousPair.first +1 , storyId)
                })
                .skip(1)//Skip the fake accumulator pair
                .flatMap({ storyIdRankPair: Pair<Int,Long> ->
                    hackerNewsApiService.getStory(storyIdRankPair.second)
                            .toObservable()
                            .map { story -> Pair(storyIdRankPair.first, story) }
                })
                .map({ storyIdRankPair : Pair<Int, Story> ->
                    storyIdRankPair.second.storyRank = storyIdRankPair.first
                    storyIdRankPair.second
                })
                .toList()
    }
}
