package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import edu.byu.uapi.model.jsonschema07.Dependency
import edu.byu.uapi.model.jsonschema07.Schema

@JsonDeserialize(
    using = DependencyMixin.Deserializer::class
)
internal interface DependencyMixin {
    class Deserializer : StdDeserializer<Dependency>(Dependency::class.java) {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Dependency {
            return when (p.currentToken) {
                JsonToken.START_ARRAY -> Dependency.PropertyListDependency(
                    p.readValueAs(stringListTypeRef)
                )
                JsonToken.START_OBJECT -> Dependency.SchemaDependency(
                    p.readValueAs(Schema::class.java)
                )
                else -> ctxt.handleUnexpectedToken(Dependency::class.java, p) as Dependency
            }
        }
    }

    interface SchemaDependency {
        @get:JsonValue
        val schema: Schema
    }

    interface PropertyListDependency {
        @get:JsonValue
        val properties: List<String>
    }
}

private val stringListTypeRef = object: TypeReference<List<String>>(){}
