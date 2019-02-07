package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import edu.byu.uapi.model.UAPIExtensible
import edu.byu.uapi.model.UAPIExtensions

internal interface UAPIExtensibleMixin: UAPIExtensible {
    @get:JsonAnyGetter
    override val extensions: UAPIExtensions

    @JsonAnySetter
    override fun addExtension(key: String, value: Any?)
}