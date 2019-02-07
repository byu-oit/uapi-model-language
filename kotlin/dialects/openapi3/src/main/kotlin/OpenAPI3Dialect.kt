package edu.byu.uapi.model.openapi3

import com.google.auto.service.AutoService
import edu.byu.uapi.model.UAPIModel
import edu.byu.uapi.model.dialect.UAPIDialect
import edu.byu.uapi.model.openapi3.serialization.OpenAPI3Reader
import edu.byu.uapi.model.openapi3.serialization.OpenAPI3Writer
import io.swagger.v3.oas.models.OpenAPI

object OpenAPI3Dialect: UAPIDialect<OpenAPI> {
    override val name: String = "openapi3"
    override val writer: OpenAPI3Writer by lazy { OpenAPI3Writer.getInstance() }
    override val reader: OpenAPI3Reader by lazy { OpenAPI3Reader.getInstance() }

    override fun convert(model: UAPIModel): OpenAPI {
        return model.toOpenApi3()
    }

    @AutoService(UAPIDialect.Loader::class)
    class Loader: UAPIDialect.Loader<OpenAPI> {
        override val dialect = OpenAPI3Dialect
    }
}

