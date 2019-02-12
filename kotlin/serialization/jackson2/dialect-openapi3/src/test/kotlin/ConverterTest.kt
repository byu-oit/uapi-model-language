import edu.byu.uapi.model.dialect.UAPIDefaultDialect
import edu.byu.uapi.model.dialect.UAPIDialect
import edu.byu.uapi.model.openapi3.OpenAPI3Dialect
import edu.byu.uapi.model.serialization.UAPIModelReader
import edu.byu.uapi.model.serialization.UAPISerializationFormat
import io.swagger.v3.oas.models.OpenAPI
import java.io.File
import java.nio.file.Paths

fun main() {
    println(UAPIDialect.findAllDialects())
    val uapiReader = UAPIModelReader.getInstance()

    val personsModel = uapiReader.read(persons, UAPISerializationFormat.YAML)

    File("gen-persons-uapi.json")
        .bufferedWriter().use {
            UAPIDefaultDialect.writer.write(personsModel, it, UAPISerializationFormat.JSON, true)
        }

    val f = File("gen-persons-openapi3.yml")

    f.bufferedWriter().use {
        OpenAPI3Dialect.convertAndWrite(personsModel, it, UAPISerializationFormat.YAML, true)
    }

    val cardsModel = uapiReader.read(cards)

    File("gen-campus-cards-openapi3.yml").bufferedWriter().use {
        OpenAPI3Dialect.convertAndWrite(cardsModel, it, UAPISerializationFormat.YAML, true)
    }
}

val persons = Paths.get(System.getProperty("user.dir"), "examples", "persons-v3.yml")
    .normalize().toFile().readText()

val cards = """{"info":{"name":"UAPI Runtime","version":"0.1.0"},"resources":{"books":{"keys":["oclc"],"properties":{"oclc":{"value":{"type":"big-int"},"api_types":["read-only"]},"title":{"value":{"type":"string"},"api_types":["read-only","modifiable"]},"publisher_id":{"value":{"type":"int"},"api_types":["read-only","modifiable"]},"available_copies":{"value":{"type":"int"},"api_types":["derived"]},"isbn":{"value":{"type":"string"},"api_types":["system"]},"subtitles":{"value_array":{"items":{"type":"string"}},"api_types":["read-only","modifiable"]},"author_ids":{"value_array":{"items":{"type":"int"}},"api_types":["read-only","modifiable"]},"genres":{"value_array":{"items":{"type":"string"}},"api_types":["read-only","modifiable"]},"published_year":{"value":{"type":"int"},"api_types":["read-only","modifiable"]},"restricted":{"value":{"type":"boolean"},"api_types":["read-only","modifiable"]}},"singular_name":"book","list":{"subset":{"default_size":50,"max_size":100},"filters":{"authors.ids":{"type":"int","allow_multiple":true},"authors.names":{"type":"string","allow_multiple":true},"genres.codes":{"type":"string","allow_multiple":true},"genres.names":{"type":"string","allow_multiple":true},"isbns":{"type":"string","allow_multiple":true},"publication_year":{"type":"int"},"publisher_ids":{"type":"int","allow_multiple":true},"publisher_names":{"type":"string","allow_multiple":true},"restricted":{"type":"boolean"},"subtitle":{"type":"string"},"title":{"type":"string"}},"sorting":{"available_sort_properties":["oclc","title","publisher_name","isbn","published_year","author_name"],"default_sort_properties":["title","oclc"],"default_sort_order":"ascending"},"search":{"search_contexts_available":{"titles":["subtitles","title"],"authors":["authors.name"],"genres":["genreCodes.codes","genreCodes.name"],"control_numbers":["isbn","oclc"]}}},"create":{"input":{"application/json":{"${'$'}schema":"http://json-schema.org/draft-04/schema#","title":"Create Book","required":["oclc","title","subtitles","published_year","publisher_id","author_ids","genre_codes","restricted"],"additionalProperties":false,"properties":{"oclc":{"type":"integer"},"isbn":{"type":"string"},"title":{"minLength":1,"pattern":"^.*\\S+.*${'$'}","type":"string"},"subtitles":{"items":{"type":"string"},"type":"array"},"published_year":{"type":"integer"},"publisher_id":{"type":"integer"},"author_ids":{"items":{"type":"integer"},"type":"array"},"genre_codes":{"items":{"type":"string"},"type":"array"},"restricted":{"type":"boolean"}},"type":"object"}}},"update":{"input":{}},"delete":{},"type":"list"}},"${'$'}uapi":"0.1","${'$'}schema":"http://api.byu.edu/uapi/v0.1"}"""