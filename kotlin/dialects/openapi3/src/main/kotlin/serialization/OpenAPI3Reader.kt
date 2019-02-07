package edu.byu.uapi.model.openapi3.serialization

import io.swagger.v3.oas.models.OpenAPI
import serialization.UAPIReader
import serialization.UAPIServiceLoader

interface OpenAPI3Reader: UAPIReader<OpenAPI> {
    companion object: UAPIServiceLoader<OpenAPI3Reader>(OpenAPI3Reader::class)
}