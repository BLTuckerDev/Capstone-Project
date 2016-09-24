package dev.bltucker.nanodegreecapstone.injection;

import dagger.Module;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;

@Module
@ApplicationScope
public class TestApplicationResourcesModule extends ApplicationResourcesModule {

    public TestApplicationResourcesModule(CapstoneApplication application) {
        super(application);
    }
}
