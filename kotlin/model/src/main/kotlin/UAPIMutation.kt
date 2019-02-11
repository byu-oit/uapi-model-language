package edu.byu.uapi.model


sealed class UAPIMutation : UAPIDocumentable, UAPICommentable, UAPIExtensible

data class UAPICreateMutation(
    val input: UAPIInput,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIMutation()

data class UAPIUpdateMutation(
    val input: UAPIInput,
    val createsIfMissing: Boolean = false,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIMutation()

data class UAPIDeleteMutation(
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIMutation()
