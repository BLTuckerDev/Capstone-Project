package dev.bltucker.nanodegreecapstone;

import dev.bltucker.nanodegreecapstone.injection.ApplicationComponent;
import dev.bltucker.nanodegreecapstone.injection.ApplicationResourcesModule;
import dev.bltucker.nanodegreecapstone.injection.DaggerApplicationComponent;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;

public class MockCapstoneApplication extends CapstoneApplication {


    @Override
    protected void createApplicationComponent() {
        final ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
                .applicationResourcesModule(new ApplicationResourcesModule(this))
                .build();

        DaggerInjector.initializeInjector(applicationComponent);
    }
}
