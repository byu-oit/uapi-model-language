package edu.byu.uapi.model

sealed class UAPIListFeatureModel : UAPIDocumentable, UAPIExtensible

data class UAPISubsetListFeatureModel(
    val defaultSize: Int,
    val maxSize: Int,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIListFeatureModel()

data class UAPIFiltersListFeatureModel(
//    val defaultSize: Int,
//    val maxSize: Int,
    //TODO: Document properties
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIListFeatureModel()

data class UAPISortListFeatureModel(
    val availableSortProperties: Set<String>,
    val defaultSortProperties: Set<String>,
    val defaultSortOrder: UAPISortOrder,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIListFeatureModel()

data class UAPISearchListFeatureModel(
    val searchContexts: Map<String, List<String>>,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIListFeatureModel()
