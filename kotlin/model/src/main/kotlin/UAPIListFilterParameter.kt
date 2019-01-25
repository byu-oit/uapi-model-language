package edu.byu.uapi.model

data class UAPIListFilterParameter(
    val name: String,
    val type: UAPIValueType,
    val constraints: UAPIValueConstraints? = null,
    val supportedOperators: Set<UAPIListFilterOperator>? = null,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
): UAPIDocumentable, UAPICommentable, UAPIExtensible