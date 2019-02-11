package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonNaming
import edu.byu.uapi.model.jsonschema07.OneOrMany
import edu.byu.uapi.model.jsonschema07.OneOrManyUnique
import edu.byu.uapi.model.jsonschema07.Schema
import edu.byu.uapi.model.jsonschema07.SimpleType

@JsonIgnoreProperties(ignoreUnknown = false)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonNaming(value = PropertyNamingStrategy::class /* Lower camel case */)
internal interface SchemaMixin {
    @get:JsonDeserialize(using = OneOrManyUniqueSimpleTypeDeser::class)
    val type: OneOrManyUnique<SimpleType>
    @get:JsonDeserialize(using = OneOrManySchemaDeser::class)
    val items: OneOrMany<Schema>

    class OneOrManyUniqueSimpleTypeDeser : OneOrManyUniqueMixin.OneOrManyUniqueDeserializer<SimpleType>(
        object : TypeReference<SimpleType>() {},
        object : TypeReference<List<SimpleType>>() {}
    )

    class OneOrManySchemaDeser : OneOrManyMixin.OneOrManyDeserializer<Schema>(
        object : TypeReference<Schema>() {},
        object : TypeReference<List<Schema>>() {}
    )
}