package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.annotation.JsonValue
import edu.byu.uapi.model.UAPIEnum

internal interface UAPIEnumMixin: UAPIEnum {
    @get:JsonValue
    override val apiValue: String
}