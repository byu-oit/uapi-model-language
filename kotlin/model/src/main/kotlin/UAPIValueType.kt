package edu.byu.uapi.model

enum class UAPIValueType {
    INT, // 4-byte integer (equivalent to kotlin.Int)
    BIG_INT, // 8-byte integer (equivalent to kotlin.Long)
    DECIMAL, // 4-byte float (equivalent to kotlin.Float)
    BIG_DECIMAL, // 8-byte float (equivalent to kotlin.Double)
    STRING,
    BOOLEAN,
    DATE, // RFC3339 full-date
    DATE_TIME, // RFC3339 date-time
    BINARY_STRING // Base64-encoded binary data
}


