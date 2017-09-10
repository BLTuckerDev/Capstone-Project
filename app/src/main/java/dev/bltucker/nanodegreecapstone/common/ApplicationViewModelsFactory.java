package dev.bltucker.nanodegreecapstone.common;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import dev.bltucker.nanodegreecapstone.injection.ApplicationScope;

@ApplicationScope
public class ApplicationViewModelsFactory implements ViewModelProvider.Factory{

    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> creators;

    @Inject
    public ApplicationViewModelsFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators){
        this.creators = creators;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        Provider<? extends ViewModel> creator = creators.get(modelClass);

        if (creator == null) {
            throw new IllegalArgumentException("unknown model class " + modelClass);
        }
        try {
            return (T) creator.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}