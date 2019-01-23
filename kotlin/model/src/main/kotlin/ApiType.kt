package edu.byu.uapi.model

enum class ApiType(private val apiValue: String) {
    READ_ONLY("read-only"),
    MODIFIABLE("modifiable"),
    SYSTEM("system"),
    DERIVED("derived"),
    RELATED("related");

    override fun toString(): String {
        return apiValue
    }
}