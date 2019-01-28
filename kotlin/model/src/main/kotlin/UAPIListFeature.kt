package edu.byu.uapi.model

data class UAPIListSubsetFeature(
    val defaultSize: Int,
    val maxSize: Int,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
): UAPIDocumentable, UAPICommentable, UAPIExtensible

typealias UAPIListFiltersFeature = Map<String, UAPIListFilterParameter>

data class UAPIListSortFeature(
    val availableSortProperties: List<String>,
    val defaultSortProperties: List<String>,
    val defaultSortOrder: UAPISortOrder = UAPISortOrder.ASCENDING,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
): UAPIDocumentable, UAPICommentable, UAPIExtensible

typealias UAPISearchContexts = Map<String, List<String>>

data class UAPIListSearchFeature(
    val searchContextsAvailable: UAPISearchContexts,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
): UAPIDocumentable, UAPICommentable, UAPIExtensible
