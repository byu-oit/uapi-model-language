import edu.byu.uapi.model.dialect.UAPIDialect
import edu.byu.uapi.model.openapi3.OpenAPI3Dialect
import edu.byu.uapi.model.serialization.UAPIModelReader
import edu.byu.uapi.model.serialization.UAPISerializationFormat
import java.io.File
import java.nio.file.Paths

fun main() {
    println(UAPIDialect.findAllDialects())
    val uapiReader = UAPIModelReader.getInstance()

    val personsModel = uapiReader.read(persons, UAPISerializationFormat.YAML)

    val f = File("gen-persons-openapi3.yml")

    f.bufferedWriter().use {
        OpenAPI3Dialect.convertAndWrite(personsModel, it, UAPISerializationFormat.YAML, true)
    }
}

val persons = Paths.get(System.getProperty("user.dir"), "examples", "persons-v3.yml")
    .normalize().toFile().readText()