package edu.byu.uapi.model

enum class UAPISortOrder {
    ASCENDING, DESCENDING;

    override fun toString(): String = name.toLowerCase()
}
