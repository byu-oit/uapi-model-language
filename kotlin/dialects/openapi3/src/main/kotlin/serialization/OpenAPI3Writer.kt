package edu.byu.uapi.model.openapi3.serialization

import io.swagger.v3.oas.models.OpenAPI
import serialization.UAPIServiceLoader
import serialization.UAPIWriter

interface OpenAPI3Writer: UAPIWriter<OpenAPI> {
    companion object: UAPIServiceLoader<OpenAPI3Writer>(OpenAPI3Writer::class)
}