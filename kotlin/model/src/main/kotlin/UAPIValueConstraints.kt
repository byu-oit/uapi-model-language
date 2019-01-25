package edu.byu.uapi.model

data class UAPIValueConstraints(
    val pattern: String? = null,
    val maximum: Number? = null,
    val minimum: Number? = null,
    val exclusiveMaximum: Number? = null,
    val exclusiveMinimum: Number? = null,
    val maxLength: Int? = null,
    val minLength: Int? = null,
    val enum: Set<String>? = null,
    override val documentation: String?,
    override val `$comment`: String?,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIDocumentable, UAPICommentable, UAPIExtensible