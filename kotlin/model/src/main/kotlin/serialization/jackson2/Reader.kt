package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import edu.byu.uapi.model.UAPIModel

private val jsonMapper: ObjectMapper by lazy {
    ObjectMapper().prepare()
}

private fun <M : ObjectMapper> M.prepare(): M {
    registerModule(UAPIModelModule())
    registerKotlinModule()
    setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT)
    propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    return this
}

private val yamlMapper: YAMLMapper by lazy {
    YAMLMapper().prepare()
}

fun readUAPIModelFrom(body: String, format: InputFormat = InputFormat.JSON): UAPIModel {
    val mapper = when (format) {
        InputFormat.JSON -> jsonMapper
        InputFormat.YAML -> yamlMapper
    }
    return mapper.readValue(body)
}

enum class InputFormat {
    JSON, YAML
}
