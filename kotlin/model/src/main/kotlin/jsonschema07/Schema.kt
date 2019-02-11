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
    val items: OneOrMany<Schema>? = null,
    val additionalItems: Schema? = null,
    val maxItems: BigInteger? = null,
    val minItems: BigInteger = BigInteger.ZERO,
    val uniqueItems: Boolean = false,
    val contains: Schema? = null,
    val maxProperties: BigInteger? = null,
    val minProperties: BigInteger = BigInteger.ZERO,
    val required: List<String> = emptyList(),
    val additionalProperties: Schema? = null,
    // val definitions: Map<String, Schema> = emptyMap(), TODO: Refs?
    val properties: Map<String, Schema> = emptyMap(),
    val patternProperties: Map<String, Schema> = emptyMap(),
    val dependencies: Map<String, Dependency> = emptyMap(),
    val propertyNames: Schema? = null, //TODO?
    val const: Any? = null,
    val enum: Set<Any> = emptySet(),
    val type: OneOrManyUnique<SimpleType>? = null,
    val format: Format? = null,
    val contentMediaType: String? = null,
    val contentEncoding: String? = null,
    val `if`: Schema? = null,
    val then: Schema? = null,
    val `else`: Schema? = null,
    val allOf: List<Schema> = emptyList(),
    val anyOf: List<Schema> = emptyList(),
    val oneOf: List<Schema> = emptyList(),
    val not: Schema? = null
)

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
}

sealed class OneOrManyUnique<T> {
    data class One<T>(val value: T) : OneOrManyUnique<T>()
    data class Many<T>(val values: Set<T>) : OneOrManyUnique<T>()
}

sealed class Format(open val value: String) {
    object DateTime : Format("date-time")
    object Date : Format("date")
    object Time : Format("time")
    object Email : Format("email")
    object IdnEmail : Format("idn-email")
    object Hostname : Format("hostname")
    object IdnHostname : Format("idn-hostname")
    object IpV4 : Format("ipv4")
    object IpV6 : Format("ipv6")
    object Uri : Format("uri")
    object UriReference : Format("uri-reference")
    object Iri : Format("iri")
    object IriReference : Format("iri-reference")
    object UriTemplate : Format("uri-template")
    object JsonPointer : Format("json-pointer")
    object RelativeJsonPointer : Format("relative-json-pointer")
    object Regex : Format("regex")

    data class Other(override val value: String) : Format(value) {
        override fun toString(): String {
            return super.toString()
        }
    }

    override fun toString(): String {
        return "Format($value)"
    }
}