package dev.bltucker.nanodegreecapstone.common

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.bltucker.nanodegreecapstone.injection.ApplicationScope
import javax.inject.Inject
import javax.inject.Provider

@ApplicationScope
class ApplicationViewModelsFactory @Inject constructor(private val creators: Map<Class<out ViewModel>, Provider<ViewModel>>)
    : ViewModelProvider.Factory {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: throw IllegalArgumentException("unknown model class " + modelClass)

        try {
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

}