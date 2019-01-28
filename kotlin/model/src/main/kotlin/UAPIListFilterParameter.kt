package edu.byu.uapi.model

data class UAPIListFilterParameter(
    val type: UAPIValueType,
    val allowMultiple: Boolean = false,
    val constraints: UAPIValueConstraints? = null,
    val supportedOperators: Set<UAPIListFilterOperator>? = null,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
): UAPIDocumentable, UAPICommentable, UAPIExtensible