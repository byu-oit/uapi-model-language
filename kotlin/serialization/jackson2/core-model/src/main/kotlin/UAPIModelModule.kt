package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.Serializers
import com.fasterxml.jackson.databind.ser.std.StdSerializer
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

        context.addDeserializers(ValueOrBoolDeserializers)
        context.addSerializers(ValueOrBooleanSerializers)
    }

    private fun SetupContext.mixin(
        type: KClass<*>,
        mixin: KClass<*>
    ) {
        this.setMixInAnnotations(type.java, mixin.java)
    }
}

object ValueOrBoolDeserializers : Deserializers.Base() {
    override fun findBeanDeserializer(
        type: JavaType,
        config: DeserializationConfig,
        beanDesc: BeanDescription
    ): JsonDeserializer<*>? {
        return if (type.isTypeOrSubTypeOf(ValueOrBool::class.java)) {
            val contents = type.containedTypeOrUnknown(0)
            ValueOrBoolDeser<Any>(contents)
        } else {
            super.findBeanDeserializer(type, config, beanDesc)
        }
    }
}

object ValueOrBooleanSerializers : Serializers.Base() {
    override fun findSerializer(
        config: SerializationConfig,
        type: JavaType,
        beanDesc: BeanDescription
    ): JsonSerializer<*>? {
        if (type.isTypeOrSubTypeOf(ValueOrBool::class.java)) {
            return ValueOrBoolSer()
        }
        return super.findSerializer(config, type, beanDesc)
    }
}

internal class ValueOrBoolDeser<T>(
    private val contentType: JavaType
) : StdDeserializer<ValueOrBool<T>>(ValueOrBool::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ValueOrBool<T> {
        return when (p.currentToken) {
            JsonToken.VALUE_TRUE -> ValueOrBool.TRUE
            JsonToken.VALUE_FALSE -> ValueOrBool.FALSE
            else -> ValueOrBool.Value(ctxt.readValue(p, contentType))
        }
    }
}

internal class ValueOrBoolSer: StdSerializer<ValueOrBool<*>>(ValueOrBool::class.java, true) {
    override fun serialize(value: ValueOrBool<*>, gen: JsonGenerator, provider: SerializerProvider) {
        when (value) {
            is ValueOrBool.Value -> gen.writeObject(value.value)
            is ValueOrBool.Bool -> gen.writeBoolean(value.value)
        }.exhaustive
    }
}

private val <T> T.exhaustive: T
    get() = this
