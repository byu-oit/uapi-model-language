package edu.byu.uapi.model

enum class UAPIListFilterOperator(
    override val apiValue: String
): UAPIEnum {
    STARTS_WITH("starts_with"),
    ENDS_WITH("ends_with"),
    CONTAINS("contains"),
    GREATER_THAN("gt"),
    GREATER_THAN_OR_EQUAL("gt_or_eq"),
    LESS_THAN("lt"),
    LESS_THAN_OR_EQUAL("lt_or_eq"),
    NOT_EQUAL("not_eq"),
    IS_NULL("is_null"),
    IS_EMPTY("is_empty"),
    NOT_IN("not_in")
}