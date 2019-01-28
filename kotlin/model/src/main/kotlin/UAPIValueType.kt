package edu.byu.uapi.model

enum class UAPIValueType(
    override val apiValue: String
): UAPIEnum {
    INT("int"),
    BIG_INT("big-int"),
    DECIMAL("decimal"),
    BIG_DECIMAL("big-decimal"),
    STRING("string"),
    BOOLEAN("boolean"),
    DATE("date"),
    DATE_TIME("date-time"),
    BYTE_ARRAY("byte-array");
}