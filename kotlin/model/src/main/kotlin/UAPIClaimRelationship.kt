package edu.byu.uapi.model

enum class UAPIClaimRelationship(
    override val apiValue: String
) : UAPIEnum {
    GREATER_THAN_OR_EQUAL("gt_or_eq"),
    GREATER_THAN("gt"),
    EQUAL("eq"),
    NOT_EQUAL("not_eq"),
    LESS_THAN("lt"),
    LESS_THAN_OR_EQUAL("lt_or_eq")
}