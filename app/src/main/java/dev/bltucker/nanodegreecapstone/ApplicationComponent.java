package dev.bltucker.nanodegreecapstone;

import dagger.Component;
import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter;

@ApplicationScope
@Component(modules = { ApplicationResourcesModule.class})
public interface ApplicationComponent {
    void inject(StorySyncAdapter syncAdapter);
    void inject(StoryListFragment fragment);
}
