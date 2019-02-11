package edu.byu.uapi.model

import edu.byu.uapi.model.jsonschema07.Schema

//class UAPIInput(
//    map: Map<String, Schema> = emptyMap()
//) : Map<String, Schema> by map {
//    val json: Schema?
//        get() = this["application/json"]
//
//    val default: Schema?
//        get() = this["*/*"]
//}

data class UAPIInput(
    val json: Schema? = null,
    val others: Map<String, Any> = emptyMap()
)