package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.util.StdConverter
import edu.byu.uapi.model.UAPIInput
import edu.byu.uapi.model.jsonschema07.Schema

@JsonSerialize(converter = UAPIInputMixin.OutConverter::class)
@JsonDeserialize(converter = UAPIInputMixin.InConverter::class)
internal interface UAPIInputMixin {
    class InConverter : StdConverter<JsonInput, UAPIInput>() {
        override fun convert(value: JsonInput): UAPIInput {
            return UAPIInput(value.application_json, value.others)
        }
    }

    class OutConverter : StdConverter<UAPIInput, JsonInput>() {
        override fun convert(value: UAPIInput): JsonInput {
            return JsonInput().apply {
                application_json = value.json
                others = value.others
            }
        }
    }

    class JsonInput {
        @JsonProperty("application/json")
        var application_json: Schema? = null

        @get:JsonAnyGetter
        @set:JsonAnySetter
        var others: Map<String, Any> = emptyMap()
    }
}