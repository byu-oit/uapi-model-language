package edu.byu.uapi.model

data class UAPISearchContexts(
    val properties: Map<String, Set<String>>,
    override val documentation: String? = null,
    override val `$comment`: String? = null
): UAPIDocumentable, UAPICommentable