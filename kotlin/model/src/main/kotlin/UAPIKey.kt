package edu.byu.uapi.model

data class UAPIKey(
    val name: String,
    val type: UAPIValueType,
    val constraints: UAPIValueConstraints? = null,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
): UAPIDocumentable, UAPICommentable, UAPIExtensible