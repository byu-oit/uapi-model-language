package edu.byu.uapi.model

enum class UAPIResourceType(
    override val apiValue: String
) : UAPIEnum {
    LIST("list"),
    SINGLETON("singleton")
}