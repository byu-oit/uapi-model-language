package edu.byu.uapi.model

import java.util.*

data class UAPIClaimModel(
    val type: UAPIValueType,
    val constraints: UAPIValueConstraints? = null,
    val qualifiers: Map<String, UAPIClaimQualifier> = emptyMap(),
    val relationships: Set<UAPIClaimRelationship> = EnumSet.allOf(UAPIClaimRelationship::class.java)
)

