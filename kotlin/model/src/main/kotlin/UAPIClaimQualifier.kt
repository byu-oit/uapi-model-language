package edu.byu.uapi.model

data class UAPIClaimQualifier(
    val type: UAPIValueType,
    val constraints: UAPIValueConstraints? = null,
    val default: Any? = null,
    val allowMultiple: Boolean = false
)
