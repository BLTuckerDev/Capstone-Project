package dev.bltucker.nanodegreecapstone.storydetail

import com.jakewharton.rxrelay2.BehaviorRelay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryDetailViewModel @Inject constructor(val modelRelay: BehaviorRelay<StoryDetailModel>){


    fun onStoryDetailSelected(detailStory: DetailStory){
        modelRelay.accept(StoryDetailModel(detailStory, null, false, null))

        //TODO
        //comments repository
        //that checks for offline ones and returns them first
        //but also goes remote and gets the latest and then emites that.
    }


}