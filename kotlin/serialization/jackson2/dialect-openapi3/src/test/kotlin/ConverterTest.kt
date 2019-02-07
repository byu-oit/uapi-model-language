import edu.byu.uapi.model.openapi3.serialization.OpenAPI3Writer
import edu.byu.uapi.model.openapi3.toOpenApi3
import edu.byu.uapi.model.serialization.UAPIModelReader
import edu.byu.uapi.model.serialization.UAPISerializationFormat
import java.io.File
import java.nio.file.Paths

fun main() {
    val uapiReader = UAPIModelReader.getInstance()
    val oapiWriter = OpenAPI3Writer.getInstance()

    val personsModel = uapiReader.read(persons, UAPISerializationFormat.YAML)

    val openApi = personsModel.toOpenApi3()

    val f = File("gen-persons-openapi3.yml")

    f.bufferedWriter().use {
        oapiWriter.write(openApi, it, UAPISerializationFormat.YAML, true)
    }
}

val persons = Paths.get(System.getProperty("user.dir"), "examples", "persons-v3.yml")
    .normalize().toFile().readText()