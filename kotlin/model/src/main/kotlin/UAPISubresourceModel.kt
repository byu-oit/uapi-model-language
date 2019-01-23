package edu.byu.uapi.model

sealed class UAPISubresourceModel : UAPIDocumentable, UAPIExtensible {
    abstract val name: String

    abstract val properties: List<UAPIPropertyModel>

    abstract val create: UAPICreateMutationModel?
    abstract val update: UAPIUpdateMutationModel?
    abstract val delete: UAPIDeleteMutationModel?
}

data class UAPIListSubresourceModel(
    override val name: String,
    override val properties: List<UAPIPropertyModel>,
    val keys: List<UAPIKeyFieldModel>,
    val list: UAPIListFeatureModel,
    override val create: UAPICreateMutationModel? = null,
    override val update: UAPIUpdateMutationModel? = null,
    override val delete: UAPIDeleteMutationModel? = null,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPISubresourceModel()

data class UAPISingletonSubresourceModel(
    override val name: String,
    override val properties: List<UAPIPropertyModel>,
    override val create: UAPICreateMutationModel? = null,
    override val update: UAPIUpdateMutationModel? = null,
    override val delete: UAPIDeleteMutationModel? = null,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPISubresourceModel()

