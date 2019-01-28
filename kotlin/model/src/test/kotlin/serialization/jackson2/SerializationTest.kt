package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import edu.byu.uapi.model.UAPIApiType
import edu.byu.uapi.model.UAPIProperty
import edu.byu.uapi.model.UAPIValuePropertyDefinition
import edu.byu.uapi.model.UAPIValueType

fun main() {
    val prop = UAPIProperty(
        definition = UAPIValuePropertyDefinition(
            UAPIValueType.BIG_DECIMAL
        ),
        apiTypes = setOf(UAPIApiType.SYSTEM),
        `$comment` = "hello there",
        extensions = mutableMapOf("x-foo" to "hi", "x-bar" to 0.1)
    )

    val om = ObjectMapper()
    om.registerModule(UAPIModelModule())
    om.registerKotlinModule()
    om.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS)
    om.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT)
    om.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE

    val json = om.writerWithDefaultPrettyPrinter().writeValueAsString(prop)

    println(json)

    val read = om.readValue<UAPIProperty>(json)
    println(read)
    println(read.extensions)
}
