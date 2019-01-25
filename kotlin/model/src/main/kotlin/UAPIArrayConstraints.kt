package edu.byu.uapi.model

data class UAPIArrayConstraints(
    val minItems: Int? = null,
    val maxItems: Int? = null,
    val uniqueItems: Boolean = false,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
            override val extensions: UAPIExtensions = mutableMapOf()
): UAPIDocumentable, UAPICommentable, UAPIExtensible