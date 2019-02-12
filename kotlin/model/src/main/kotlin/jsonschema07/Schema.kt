package edu.byu.uapi.model.jsonschema07

import edu.byu.uapi.model.UAPIEnum
import java.math.BigDecimal
import java.math.BigInteger

data class Schema(
    val `$schema`: String = "http://json-schema.org/draft-07/schema#",
    val `$id`: String = "http://json-schema.org/draft-07/schema#",
    // val `$ref`: String? = null, TODO: Refs?
    val `$comment`: String? = null,
    val title: String? = null,
    val description: String? = null,
    val default: Any? = null,
    val readOnly: Boolean = false,
    val examples: List<Any> = emptyList(),
    val multipleOf: BigDecimal? = null,
    val maximum: BigDecimal? = null,
    val exclusiveMaximum: BigDecimal? = null,
    val minimum: BigDecimal? = null,
    val exclusiveMinimum: BigDecimal? = null,
    val maxLength: BigInteger? = null,
    val minLength: BigInteger = BigInteger.ZERO,
    val pattern: String? = null,
    val items: OneOrMany<ValueOrBool<Schema>> = OneOrMany(ValueOrBool.TRUE),
    val additionalItems: ValueOrBool<Schema> = ValueOrBool.TRUE,
    val maxItems: BigInteger? = null,
    val minItems: BigInteger = BigInteger.ZERO,
    val uniqueItems: Boolean = false,
    val contains: ValueOrBool<Schema> = ValueOrBool.TRUE,
    val maxProperties: BigInteger? = null,
    val minProperties: BigInteger = BigInteger.ZERO,
    val required: List<String> = emptyList(),
    val additionalProperties: ValueOrBool<Schema> = ValueOrBool.TRUE,
    // val definitions: Map<String, Schema> = emptyMap(), TODO: Refs?
    val properties: Map<String, ValueOrBool<Schema>> = emptyMap(),
    val patternProperties: Map<String, ValueOrBool<Schema>> = emptyMap(),
    val dependencies: Map<String, Dependency> = emptyMap(),
    val propertyNames: ValueOrBool<Schema> = ValueOrBool.TRUE,
    val const: Any? = null,
    val enum: Set<Any> = emptySet(),
    val type: OneOrManyUnique<SimpleType>? = null,
    val format: Format? = null,
    val contentMediaType: String? = null,
    val contentEncoding: String? = null,
    val `if`: ValueOrBool<Schema> = ValueOrBool.TRUE,
    val then: ValueOrBool<Schema> = ValueOrBool.TRUE,
    val `else`: ValueOrBool<Schema> = ValueOrBool.TRUE,
    val allOf: List<ValueOrBool<Schema>> = emptyList(),
    val anyOf: List<ValueOrBool<Schema>> = emptyList(),
    val oneOf: List<ValueOrBool<Schema>> = emptyList(),
    val not: Schema? = null
)

sealed class ValueOrBool<out T> {
    data class Value<out T>(val value: T) : ValueOrBool<T>()
    data class Bool(val value: Boolean) : ValueOrBool<Nothing>()
    companion object {
        val TRUE = ValueOrBool.Bool(true)
        val FALSE = ValueOrBool.Bool(false)

        operator fun invoke(boolean: Boolean): ValueOrBool.Bool {
            return when (boolean) {
                true -> TRUE
                false -> FALSE
            }
        }

        operator fun <T> invoke(value: T): ValueOrBool<T> {
            return ValueOrBool.Value(value)
        }
    }
}

enum class SimpleType(override val apiValue: String) : UAPIEnum {
    ARRAY("array"),
    BOOLEAN("boolean"),
    INTEGER("integer"),
    NULL("null"),
    NUMBER("number"),
    OBJECT("object"),
    STRING("string")
}

sealed class Dependency {
    data class SchemaDependency(val schema: Schema) : Dependency()
    data class PropertyListDependency(val properties: List<String>) : Dependency()
}

sealed class OneOrMany<T> {
    data class One<T>(val value: T) : OneOrMany<T>()
    data class Many<T>(val values: List<T>) : OneOrMany<T>()

    companion object {
        operator fun <T> invoke(value: T): OneOrMany<T> {
            return OneOrMany.One(value)
        }

        operator fun <T> invoke(values: List<T>): OneOrMany<T> {
            return OneOrMany.Many(values)
        }
    }
}


sealed class OneOrManyUnique<T> {
    data class One<T>(val value: T) : OneOrManyUnique<T>()
    data class Many<T>(val values: Set<T>) : OneOrManyUnique<T>()

    companion object {
        operator fun <T> invoke(value: T): OneOrManyUnique<T> {
            return OneOrManyUnique.One(value)
        }

        operator fun <T> invoke(values: Set<T>): OneOrManyUnique<T> {
            return OneOrManyUnique.Many(values)
        }
    }
}

