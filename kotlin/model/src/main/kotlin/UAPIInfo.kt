package edu.byu.uapi.model

data class UAPIInfo(
    val name: String,
    val version: String,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIDocumentable, UAPICommentable, UAPIExtensible