package edu.byu.uapi.model

data class UAPIListFeatureModel(
    val subset: UAPIListSubsetFeature? = null,
    val filters: UAPIListFiltersFeature? = null,
    val sorting: UAPIListSortFeature? = null,
    val search: UAPIListSearchFeature? = null,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
): UAPIDocumentable, UAPICommentable, UAPIExtensible