package dev.bltucker.nanodegreecapstone.injection;

public class DaggerInjector {

    private static ApplicationComponent applicationComponent;

    public static ApplicationComponent getApplicationComponent(){
        return applicationComponent;
    }


    public static void initializeInjector(ApplicationComponent applicationComponent){
        DaggerInjector.applicationComponent = applicationComponent;
    }

}
