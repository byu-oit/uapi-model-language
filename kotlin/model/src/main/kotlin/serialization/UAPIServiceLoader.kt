package serialization

import java.util.*
import kotlin.reflect.KClass

abstract class UAPIServiceLoader<T : Any>(private val type: KClass<T>) {
    private val loader by lazy { ServiceLoader.load(type.java) }

    fun getInstance(forceReload: Boolean = false): T {
        if (forceReload) {
            loader.reload()
        }
        val count = loader.count()
        return when (count) {
            0 -> throw UnableToLoadServiceException("No ServiceLoader services registered for ${type.java.name}.", null)
            1 -> loader.single()
            else -> throw UnableToLoadServiceException("Multiple ($count) ServiceLoaders registered for ${type.java.name}. Remove the extras, or explicitly choose an implementation.")
        }
    }
}

class UnableToLoadServiceException(message: String, cause: Throwable? = null) : Exception(message, cause)
