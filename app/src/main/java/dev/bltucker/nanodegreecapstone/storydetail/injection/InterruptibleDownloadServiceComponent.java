package dev.bltucker.nanodegreecapstone.storydetail.injection;

import dagger.Subcomponent;
import dev.bltucker.nanodegreecapstone.storydetail.data.InterruptibleDownloadService;

@Subcomponent(modules = {InterruptibleDownloadServiceModule.class})
@InterruptibleDownloadServiceScope
public interface InterruptibleDownloadServiceComponent {
    void inject(InterruptibleDownloadService service);
}
