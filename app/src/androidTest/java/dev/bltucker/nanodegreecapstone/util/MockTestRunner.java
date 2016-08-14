package dev.bltucker.nanodegreecapstone.util;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import dev.bltucker.nanodegreecapstone.MockCapstoneApplication;

public class MockTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
//        return super.newApplication(cl, MockCapstoneApplication.class.getName(), context);
       return Instrumentation.newApplication(MockCapstoneApplication.class, context);
    }
}
