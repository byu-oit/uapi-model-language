package edu.byu.uapi.model

data class UAPIListModel(
    val subset: UAPISubsetListFeatureModel? = null,
    val sort: UAPISortListFeatureModel? = null,
    val filters: UAPIFiltersListFeatureModel? = null,
    val search: UAPISearchListFeatureModel? = null,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIDocumentable, UAPIExtensible
