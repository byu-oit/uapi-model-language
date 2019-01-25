package edu.byu.uapi.model

enum class UAPISortOrder(
    override val apiValue: String
) : UAPIEnum {
    ASCENDING("ascending"),
    DESCENDING("descending")
}