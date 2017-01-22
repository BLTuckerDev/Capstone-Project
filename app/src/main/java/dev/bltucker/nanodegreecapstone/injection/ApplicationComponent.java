package dev.bltucker.nanodegreecapstone.injection;

import dagger.Component;
import dev.bltucker.nanodegreecapstone.home.MainActivity;
import dev.bltucker.nanodegreecapstone.location.GeofenceCreationIntentServiceComponent;
import dev.bltucker.nanodegreecapstone.location.GeofenceCreationIntentServiceModule;
import dev.bltucker.nanodegreecapstone.location.GeofenceTransitionsIntentService;
import dev.bltucker.nanodegreecapstone.readlater.ReadLaterComponent;
import dev.bltucker.nanodegreecapstone.readlater.ReadLaterListFragmentModule;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailFragmentComponent;
import dev.bltucker.nanodegreecapstone.storydetail.StoryDetailFragmentModule;
import dev.bltucker.nanodegreecapstone.storydetail.data.StoryCommentDownloadService;
import dev.bltucker.nanodegreecapstone.sync.CommentCleaningService;
import dev.bltucker.nanodegreecapstone.sync.StorySyncAdapter;
import dev.bltucker.nanodegreecapstone.topstories.StoryListFragment;
import dev.bltucker.nanodegreecapstone.topstories.TopStoriesModule;

@ApplicationScope
@Component(modules = { ApplicationResourcesModule.class, TopStoriesModule.class})
public interface ApplicationComponent {
    void inject(MainActivity mainActivity);
    void inject(StorySyncAdapter syncAdapter);
    void inject(StoryListFragment fragment);
    void inject(GeofenceTransitionsIntentService geofenceTransitionsIntentService);
    void inject(StoryCommentDownloadService storyCommentDownloadService);//TODO consider its own scoped component
    void inject(CommentCleaningService commentCleaningService);

    GeofenceCreationIntentServiceComponent geofenceCreationIntentServiceComponent(GeofenceCreationIntentServiceModule module);
    ReadLaterComponent readLaterComponent(ReadLaterListFragmentModule module);
    StoryDetailFragmentComponent storyDetailComponent(StoryDetailFragmentModule module);

}
