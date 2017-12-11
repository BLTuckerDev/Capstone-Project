package dev.bltucker.nanodegreecapstone.common.injection;

import dagger.Component;
import dev.bltucker.nanodegreecapstone.data.StoryProvider;
import dev.bltucker.nanodegreecapstone.data.HackerNewsApiService;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.home.MainActivity;
import dev.bltucker.nanodegreecapstone.location.GeofenceCreationIntentServiceComponent;
import dev.bltucker.nanodegreecapstone.location.GeofenceCreationIntentServiceModule;
import dev.bltucker.nanodegreecapstone.location.GeofenceTransitionsIntentService;
import dev.bltucker.nanodegreecapstone.readlater.ReadLaterComponent;
import dev.bltucker.nanodegreecapstone.readlater.ReadLaterListFragmentModule;
import dev.bltucker.nanodegreecapstone.sync.CommentCleaningService;
import dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentComponent;
import dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentModule;
import dev.bltucker.nanodegreecapstone.topstories.TopStoriesFragment;

@ApplicationScope
@Component(modules = {ApplicationResourcesModule.class, ViewModelModule.class, SchedulersModule.class})
public interface ApplicationComponent {
    void inject(MainActivity mainActivity);

    void inject(TopStoriesFragment fragment);

    void inject(GeofenceTransitionsIntentService geofenceTransitionsIntentService);

    void inject(CommentCleaningService commentCleaningService);

    void inject(StoryProvider storyProvider);

    HackerNewsApiService hackerNewsApiService();

    EventBus eventBus();

    GeofenceCreationIntentServiceComponent geofenceCreationIntentServiceComponent(GeofenceCreationIntentServiceModule module);

    ReadLaterComponent readLaterComponent(ReadLaterListFragmentModule module);

    StoryDetailFragmentComponent storyDetailComponent(StoryDetailFragmentModule module);

}
