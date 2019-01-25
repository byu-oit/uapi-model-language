package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.databind.module.SimpleModule
import edu.byu.uapi.model.*
import serialization.jackson2.UAPIEnumMixin
import kotlin.reflect.KClass

class UAPIModelModule: SimpleModule(
    "UAPIModel"
) {
    override fun setupModule(context: SetupContext) {
        super.setupModule(context)
        context.mixin(UAPIExtensible::class, UAPIExtensibleMixin::class)
        context.mixin(UAPIEnum::class, UAPIEnumMixin::class)
        context.mixin(UAPIProperty::class, UAPIPropertyMixin::class)
    }

    private fun SetupContext.mixin(
        type: KClass<*>,
        mixin: KClass<*>
    ) {
        this.setMixInAnnotations(type.java, mixin.java)
    }
}

