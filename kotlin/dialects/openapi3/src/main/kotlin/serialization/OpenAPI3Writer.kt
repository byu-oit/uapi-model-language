package edu.byu.uapi.model.openapi3.serialization

import edu.byu.uapi.model.serialization.UAPIServiceLoader
import edu.byu.uapi.model.serialization.UAPIWriter
import io.swagger.v3.oas.models.OpenAPI

interface OpenAPI3Writer: UAPIWriter<OpenAPI> {
    companion object: UAPIServiceLoader<OpenAPI3Writer>(OpenAPI3Writer::class)
}