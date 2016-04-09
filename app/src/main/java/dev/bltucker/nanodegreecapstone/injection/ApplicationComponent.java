package dev.bltucker.nanodegreecapstone.injection;

import dagger.Component;
import dev.bltucker.nanodegreecapstone.home.MainActivity;
import dev.bltucker.nanodegreecapstone.topstories.StoryListFragment;
import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter;

@ApplicationScope
@Component(modules = { ApplicationResourcesModule.class})
public interface ApplicationComponent {
    void inject(MainActivity mainActivity);
    void inject(StorySyncAdapter syncAdapter);
    void inject(StoryListFragment fragment);
}
