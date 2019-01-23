package edu.byu.uapi.model

data class UAPIKeyFieldModel(
    val name: String,
    override val type: UAPIValueType,
    override val constraints: UAPIValueConstraints? = null,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIValueSchema, UAPIDocumentable, UAPIExtensible
