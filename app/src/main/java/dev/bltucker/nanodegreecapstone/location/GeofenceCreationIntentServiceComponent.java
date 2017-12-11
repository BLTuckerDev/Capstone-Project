package dev.bltucker.nanodegreecapstone.location;

import dagger.Subcomponent;

@Subcomponent(modules = {GeofenceCreationIntentServiceModule.class})
public interface GeofenceCreationIntentServiceComponent {
    void inject(GeofenceCreationService service);
}
