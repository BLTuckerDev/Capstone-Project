package dev.bltucker.nanodegreecapstone.readlater;

import dagger.Subcomponent;

@Subcomponent(modules = {ReadLaterListFragmentModule.class})
@ReadLaterListFragmentScope
public interface ReadLaterComponent {
    void inject(ReadLaterListFragment fragment);
}
