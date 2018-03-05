package dev.bltucker.nanodegreecapstone.common.injection;

import org.jetbrains.annotations.NotNull;

import dagger.Component;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.common.data.StoryProvider;
import dev.bltucker.nanodegreecapstone.common.sync.OrphanCommentDeleteJob;
import dev.bltucker.nanodegreecapstone.home.HomeActivity;
import dev.bltucker.nanodegreecapstone.location.GeofenceCreationIntentServiceComponent;
import dev.bltucker.nanodegreecapstone.location.GeofenceCreationIntentServiceModule;
import dev.bltucker.nanodegreecapstone.location.GeofenceTransitionsIntentService;
import dev.bltucker.nanodegreecapstone.readlater.ReadLaterComponent;
import dev.bltucker.nanodegreecapstone.readlater.ReadLaterListFragmentModule;
import dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentComponent;
import dev.bltucker.nanodegreecapstone.storydetail.injection.StoryDetailFragmentModule;
import dev.bltucker.nanodegreecapstone.topstories.TopStoriesFragment;
import dev.bltucker.nanodegreecapstone.topstories.TopStoriesUpdateService;

@ApplicationScope
@Component(modules = {ApplicationResourcesModule.class, ViewModelModule.class, SchedulersModule.class})
public interface ApplicationComponent {

    void inject(CapstoneApplication capstoneApplication);

    void inject(HomeActivity homeActivity);

    void inject(TopStoriesFragment fragment);

    void inject(GeofenceTransitionsIntentService geofenceTransitionsIntentService);

    void inject(StoryProvider storyProvider);

    void inject(@NotNull TopStoriesUpdateService topStoriesUpdateService);

    void inject(@NotNull OrphanCommentDeleteJob orphanCommentDeleteJob);

    GeofenceCreationIntentServiceComponent geofenceCreationIntentServiceComponent(GeofenceCreationIntentServiceModule module);

    ReadLaterComponent readLaterComponent(ReadLaterListFragmentModule module);

    StoryDetailFragmentComponent storyDetailComponent(StoryDetailFragmentModule module);

}
