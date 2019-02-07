package edu.byu.uapi.model.openapi3.serialization

import edu.byu.uapi.model.serialization.UAPIReader
import edu.byu.uapi.model.serialization.UAPIServiceLoader
import io.swagger.v3.oas.models.OpenAPI

interface OpenAPI3Reader: UAPIReader<OpenAPI> {
    companion object: UAPIServiceLoader<OpenAPI3Reader>(OpenAPI3Reader::class)
}