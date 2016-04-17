package dev.bltucker.nanodegreecapstone.storydetail;

import dagger.Module;
import dagger.Provides;
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;
import dev.bltucker.nanodegreecapstone.models.ReadingSession;

@Module
@ApplicationScope
public class StoryDetailModule {

    @Provides
    @ApplicationScope
    public StoryDetailViewPresenter provideStoryDetailPresenter(ReadingSession readingSession){
        return new StoryDetailViewPresenter(readingSession);
    }
}
