package edu.byu.uapi.model

sealed class UAPISubresourceModel : UAPIDocumentable, UAPICommentable, UAPIExtensible {
    abstract val type: UAPIResourceType
}

data class UAPIListSubresourceModel(
    val keys: List<UAPIKey>,
    val properties: List<UAPIProperty>,
    val list: UAPIListFeatureModel,
    val create: UAPICreateMutation? = null,
    val update: UAPIUpdateMutation? = null,
    val delete: UAPIDeleteMutation? = null,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPISubresourceModel() {
    override val type = UAPIResourceType.LIST
}

data class UAPISingletonSubresourceModel(
    val properties: List<UAPIProperty>,
    val update: UAPIUpdateMutation? = null,
    val delete: UAPIDeleteMutation? = null,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPISubresourceModel() {
    override val type = UAPIResourceType.SINGLETON
}
