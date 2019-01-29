package edu.byu.uapi.model

sealed class UAPIResourceModel : UAPIDocumentable, UAPICommentable, UAPIExtensible {
    abstract val type: UAPIResourceType
    abstract val properties: Map<String, UAPIProperty>
    abstract val subresources: Map<String, UAPISubresourceModel>
}

data class UAPIListResourceModel(
    val keys: List<String>,
    override val properties: Map<String, UAPIProperty>,
    val singularName: String? = null,
    val list: UAPIListFeatureModel? = null,
    val create: UAPICreateMutation? = null,
    val update: UAPIUpdateMutation? = null,
    val delete: UAPIDeleteMutation? = null,
    override val subresources: Map<String, UAPISubresourceModel> = emptyMap(),
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIResourceModel() {
    override val type = UAPIResourceType.LIST
}

data class UAPISingletonResourceModel(
    override val properties: Map<String, UAPIProperty>,
    val update: UAPIUpdateMutation? = null,
    val delete: UAPIDeleteMutation? = null,
    override val subresources: Map<String, UAPISubresourceModel> = emptyMap(),
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIResourceModel() {
    override val type = UAPIResourceType.SINGLETON
}
