package edu.byu.uapi.model

data class UAPIModel(
    val info: UAPIModelInfo,
    val resources: List<UAPIResourceModel>,
    override val extensions: Map<String, Any> = emptyMap()
): UAPIExtensible

data class UAPIModelInfo(
    val name: String,
    val version: String,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIDocumentable, UAPIExtensible
