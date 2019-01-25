package edu.byu.uapi.model


sealed class UAPIMutation : UAPIDocumentable, UAPICommentable, UAPIExtensible

data class UAPICreateMutation(
    val inputSchema: UAPIInputSchema,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIMutation()

data class UAPIUpdateMutation(
    val inputSchema: UAPIInputSchema,
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
