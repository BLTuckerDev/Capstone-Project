package dev.bltucker.nanodegreecapstone.readlater;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadLaterListFragmentScope {
}
