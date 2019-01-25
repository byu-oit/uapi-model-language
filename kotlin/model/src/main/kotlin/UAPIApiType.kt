package edu.byu.uapi.model

enum class UAPIApiType(
    override val apiValue: String
): UAPIEnum {
    READ_ONLY("read-only"),
    MODIFIABLE("modifiable"),
    SYSTEM("system"),
    DERIVED("derived"),
    RELATED("related")
}