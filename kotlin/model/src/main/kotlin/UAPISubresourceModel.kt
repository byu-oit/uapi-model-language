package edu.byu.uapi.model

sealed class UAPISubresourceModel : UAPIDocumentable, UAPICommentable, UAPIExtensible {
    abstract val type: UAPIResourceType
    abstract val properties: Map<String, UAPIPropertyModel>
}

data class UAPIListSubresourceModel(
    val keys: List<String>,
    override val properties: Map<String, UAPIPropertyModel>,
    val singularName: String? = null,
    override val list: UAPIListFeatureModel? = null,
    val create: UAPICreateMutation? = null,
    val update: UAPIUpdateMutation? = null,
    val delete: UAPIDeleteMutation? = null,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPISubresourceModel(), UAPIHasListFeature {
    override val type = UAPIResourceType.LIST
}

data class UAPISingletonSubresourceModel(
    override val properties: Map<String, UAPIPropertyModel>,
    val update: UAPIUpdateMutation? = null,
    val delete: UAPIDeleteMutation? = null,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPISubresourceModel() {
    override val type = UAPIResourceType.SINGLETON
}
