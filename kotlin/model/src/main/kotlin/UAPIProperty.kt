package edu.byu.uapi.model

data class UAPIProperty(
    val name: String,
    val definition: UAPIPropertyDefinition,
    val apiTypes: Set<UAPIApiType>,
    val hasDescription: Boolean = false,
    val hasLongDescription: Boolean = false,
    val hasDisplayLabel: Boolean = false,
    val nullable: Boolean = false,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIDocumentable, UAPICommentable, UAPIExtensible
