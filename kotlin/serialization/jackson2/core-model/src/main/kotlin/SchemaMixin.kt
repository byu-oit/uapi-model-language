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
internal interface SchemaMixin