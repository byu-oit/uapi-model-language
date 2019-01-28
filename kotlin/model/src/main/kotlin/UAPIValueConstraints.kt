package edu.byu.uapi.model

import java.math.BigDecimal

data class UAPIValueConstraints(
    val pattern: String? = null,
    val maximum: BigDecimal? = null,
    val minimum: BigDecimal? = null,
    val exclusiveMaximum: Boolean = false,
    val exclusiveMinimum: Boolean = false,
    val maxLength: Int? = null,
    val minLength: Int? = null,
    val enum: Set<String>? = null,
    override val documentation: String? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIDocumentable, UAPICommentable, UAPIExtensible