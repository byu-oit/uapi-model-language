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

        context.mixin(Dependency::class, DependencyMixin::class)
        context.mixin(Dependency.SchemaDependency::class, DependencyMixin.SchemaDependency::class)
        context.mixin(Dependency.PropertyListDependency::class, DependencyMixin.PropertyListDependency::class)

        context.mixin(Schema::class, SchemaMixin::class)
        context.mixin(Format::class, FormatMixin::class)

        context.addDeserializers(UAPIDeserializers)
        context.addSerializers(UAPISerializers)
    }

    private fun SetupContext.mixin(
        type: KClass<*>,
        mixin: KClass<*>
    ) {
        this.setMixInAnnotations(type.java, mixin.java)
    }
}

object UAPIDeserializers : Deserializers.Base() {
    override fun findBeanDeserializer(
        type: JavaType,
        config: DeserializationConfig,
        beanDesc: BeanDescription
    ): JsonDeserializer<*>? {
        return when {
            type.isTypeOrSubTypeOf(ValueOrBool::class.java) -> {
                val contents = type.containedTypeOrUnknown(0)
                ValueOrBoolDeser<Any>(contents)
            }
            type.isTypeOrSubTypeOf(OneOrManyIsh::class.java) -> {
                val contents = type.containedTypeOrUnknown(0)
                val array = config.typeFactory.constructArrayType(contents)
                if (type.isTypeOrSubTypeOf(OneOrMany::class.java)) {
                    OneOrManyDeser<Any>(contents, array)
                } else {
                    OneOrManyUniqueDeser<Any>(contents, array)
                }
            }
            else -> super.findBeanDeserializer(type, config, beanDesc)
        }
    }
}

object UAPISerializers : Serializers.Base() {
    override fun findSerializer(
        config: SerializationConfig,
        type: JavaType,
        beanDesc: BeanDescription
    ): JsonSerializer<*>? {
        if (type.isTypeOrSubTypeOf(ValueOrBool::class.java)) {
            return ValueOrBoolSer()
        } else if (type.isTypeOrSubTypeOf(OneOrManyIsh::class.java)) {
            return OneOrManyIshSer()
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

internal abstract class OneOrManyBaseDeser<T, Impl: OneOrManyIsh<*, *>>(
    type: KClass<*>,
    private val contentType: JavaType,
    private val arrayType: JavaType
): StdDeserializer<Impl>(type.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Impl {
        return when (p.currentToken) {
            JsonToken.START_ARRAY -> createMany(ctxt.readValue(p, arrayType))
            else -> createOne(ctxt.readValue(p, contentType))
        }
    }

    abstract fun createOne(value: T): Impl
    abstract fun createMany(values: Collection<T>): Impl
}

internal class OneOrManyDeser<T>(
    contentType: JavaType,
    arrayType: JavaType
): OneOrManyBaseDeser<T, OneOrMany<T>>(OneOrMany::class, contentType, arrayType) {
    override fun createOne(value: T): OneOrMany<T> = OneOrMany(value)
    override fun createMany(values: Collection<T>): OneOrMany<T> = OneOrMany(values.toList())
}

internal class OneOrManyUniqueDeser<T>(
    contentType: JavaType,
    arrayType: JavaType
): OneOrManyBaseDeser<T, OneOrManyUnique<T>>(OneOrManyUnique::class, contentType, arrayType) {
    override fun createOne(value: T): OneOrManyUnique<T> = OneOrManyUnique(value)
    override fun createMany(values: Collection<T>): OneOrManyUnique<T> = OneOrManyUnique(values.toSet())
}

internal class OneOrManyIshSer: StdSerializer<OneOrManyIsh<*, *>>(OneOrManyIsh::class.java, true) {
    override fun serialize(value: OneOrManyIsh<*, *>, gen: JsonGenerator, provider: SerializerProvider) {
        value.map({
            gen.writeObject(it)
        }, {
            gen.writeStartArray()
            it.forEach(gen::writeObject)
            gen.writeEndArray()
        })
    }
}

private val <T> T.exhaustive: T
    get() = this
