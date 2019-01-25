package edu.byu.uapi.model

sealed class UAPIListFeature: UAPIDocumentable, UAPICommentable, UAPIExtensible

data class UAPIListSubsetFeature(
    val defaultSize: Int,
    val maxSize: Int,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
): UAPIListFeature()

data class UAPIListFiltersFeature(
    val parameters: List<UAPIListFilterParameter>,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
): UAPIListFeature()

data class UAPIListSortFeature(
    val availableSortProperties: Set<String>,
    val defaultSortProperties: Set<String>,
    val defaultSortOrder: UAPISortOrder = UAPISortOrder.ASCENDING,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
): UAPIListFeature()

data class UAPIListSearchFeature(
    val searchContextsAvailable: UAPISearchContexts,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
): UAPIListFeature()
