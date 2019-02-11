package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.databind.module.SimpleModule
import edu.byu.uapi.model.*
import edu.byu.uapi.model.jsonschema07.*
import kotlin.reflect.KClass

class UAPIModelModule : SimpleModule(
    "UAPIModel"
) {
    override fun setupModule(context: SetupContext) {
        super.setupModule(context)
        context.mixin(UAPIExtensible::class, UAPIExtensibleMixin::class)
        context.mixin(UAPIEnum::class, UAPIEnumMixin::class)
        context.mixin(UAPIPropertyModel::class, UAPIPropertyMixin::class)
        context.mixin(UAPIResourceModel::class, UAPIResourceModelMixin::class)
        context.mixin(UAPISubresourceModel::class, UAPISubresourceModelMixin::class)

        context.mixin(UAPIInput::class, UAPIInputMixin::class)

        context.mixin(OneOrMany.One::class, OneOrManyMixin.One::class)
        context.mixin(OneOrMany.Many::class, OneOrManyMixin.Many::class)
        context.mixin(OneOrManyUnique.One::class, OneOrManyUniqueMixin.One::class)
        context.mixin(OneOrManyUnique.Many::class, OneOrManyUniqueMixin.Many::class)

        context.mixin(Dependency::class, DependencyMixin::class)
        context.mixin(Dependency.SchemaDependency::class, DependencyMixin.SchemaDependency::class)
        context.mixin(Dependency.PropertyListDependency::class, DependencyMixin.PropertyListDependency::class)

        context.mixin(Schema::class, SchemaMixin::class)
        context.mixin(Format::class, FormatMixin::class)
    }

    private fun SetupContext.mixin(
        type: KClass<*>,
        mixin: KClass<*>
    ) {
        this.setMixInAnnotations(type.java, mixin.java)
    }
}
