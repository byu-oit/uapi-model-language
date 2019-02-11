package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import edu.byu.uapi.model.jsonschema07.OneOrMany
import edu.byu.uapi.model.jsonschema07.OneOrManyUnique
import edu.byu.uapi.model.jsonschema07.Schema
import edu.byu.uapi.model.jsonschema07.SimpleType
import kotlin.reflect.KClass

internal interface OneOrManyMixin {
    interface One {
        @get:JsonValue
        val value: Any?
    }

    interface Many {
        @get:JsonValue
        val values: List<Any?>
    }

    abstract class OneOrManyDeserializer<T>(
        singleType: TypeReference<T>,
        listType: TypeReference<List<T>>
    ) : BaseOneOrManyDeserializer<T, OneOrMany<T>>(OneOrMany::class, singleType, listType) {
        override fun createOne(value: T) = OneOrMany.One(value)
        override fun createMany(values: Collection<T>) = OneOrMany.Many(values.toList())
    }
}

internal interface OneOrManyUniqueMixin {
    interface One {
        @get:JsonValue
        val value: Any?
    }

    interface Many {
        @get:JsonValue
        val values: Set<Any?>
    }

    abstract class OneOrManyUniqueDeserializer<T>(
        singleType: TypeReference<T>,
        listType: TypeReference<List<T>>
    ) : BaseOneOrManyDeserializer<T, OneOrManyUnique<T>>(OneOrManyUnique::class, singleType, listType) {
        override fun createOne(value: T) = OneOrManyUnique.One(value)
        override fun createMany(values: Collection<T>) =
            OneOrManyUnique.Many(values.toSet())
    }
}

internal abstract class BaseOneOrManyDeserializer<T, Impl : Any>(
    type: KClass<*>,
    private val singleType: TypeReference<T>,
    private val listType: TypeReference<List<T>>
) : StdDeserializer<Impl>(type.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Impl {
        return when (p.currentToken) {
            JsonToken.START_ARRAY -> createMany(p.readValueAs(listType))
            else -> createOne(p.readValueAs(singleType))
        }
    }

    abstract fun createOne(value: T): Impl
    abstract fun createMany(values: Collection<T>): Impl
}
