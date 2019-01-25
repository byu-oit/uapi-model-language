package edu.byu.uapi.model

data class UAPIModel(
    val info: UAPIInfo,
    val resources: Map<String, UAPIResourceModel>,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
): UAPIDocumentable, UAPICommentable, UAPIExtensible {
    val `$uapi`: String = UAPIVersion
    val `$schema`: String = UAPISchema
}