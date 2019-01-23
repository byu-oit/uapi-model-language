package edu.byu.uapi.model

sealed class UAPIMutationModel : UAPIDocumentable, UAPIExtensible

data class UAPICreateMutationModel(
    val inputSchema: UAPIInputSchema,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIMutationModel()

data class UAPIUpdateMutationModel(
    val inputSchema: UAPIInputSchema,
    val createsIfIdIsMissing: Boolean = false,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIMutationModel()

data class UAPIDeleteMutationModel(
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIMutationModel()
