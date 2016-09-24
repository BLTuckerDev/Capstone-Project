package dev.bltucker.nanodegreecapstone.injection;

import dagger.Module;
import dev.bltucker.nanodegreecapstone.CapstoneApplication;

@Module
@ApplicationScope
public class MockApplicationResourcesModule extends ApplicationResourcesModule {

    public MockApplicationResourcesModule(CapstoneApplication application) {
        super(application);
    }
}
