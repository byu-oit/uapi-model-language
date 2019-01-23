package edu.byu.uapi.model

sealed class UAPIPropertyType: UAPIExtensible

data class UAPIValuePropertyType(
    override val type: UAPIValueType,
    override val constraints: UAPIValueConstraints? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIPropertyType(), UAPIValueSchema, UAPIExtensible

data class UAPIValueArrayPropertyType(
    val items: UAPIValuePropertyType,
    val constraints: UAPIArrayConstraints? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIPropertyType()

data class UAPIObjectPropertyType(
    val properties: Map<String, UAPIPropertyModel>,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIPropertyType()

data class UAPIObjectArrayPropertyType(
    val items: UAPIObjectPropertyType,
    val constraints: UAPIArrayConstraints? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIPropertyType()

data class UAPIArrayConstraints(
    val minItems: Int? = null,
    val maxItems: Int? = null,
    val uniqueItems: Boolean? = null,
    override val extensions: Map<String, Any> = emptyMap()
): UAPIExtensible

data class UAPIValueConstraints(
    val pattern: String? = null,
    val maximum: Number? = null,
    val minimum: Number? = null,
    val exclusiveMaximum: Number? = null,
    val exclusiveMinimum: Number? = null,
    val maxLength: Int? = null,
    val minLength: Int? = null,
    val enumValues: Set<String>? = null,
    override val extensions: Map<String, Any> = emptyMap()
): UAPIExtensible
