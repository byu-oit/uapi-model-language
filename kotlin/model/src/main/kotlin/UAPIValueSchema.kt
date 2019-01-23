package edu.byu.uapi.model

interface UAPIValueSchema {
    val type: UAPIValueType
    val constraints: UAPIValueConstraints?
}
