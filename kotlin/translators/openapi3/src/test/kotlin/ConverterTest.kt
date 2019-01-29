import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import edu.byu.uapi.model.openapi3.toOpenApi3
import edu.byu.uapi.model.serialization.jackson2.InputFormat
import edu.byu.uapi.model.serialization.jackson2.readUAPIModelFrom
import io.swagger.v3.core.jackson.SwaggerModule
import java.io.File
import java.nio.file.Paths

fun main() {
    val om = YAMLMapper()
        .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
        .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
        .registerModule(SwaggerModule())


    val personsModel = readUAPIModelFrom(persons, InputFormat.YAML)

    println(personsModel)

    val openApi = personsModel.toOpenApi3()
    om.writerWithDefaultPrettyPrinter().writeValue(
        File("gen-persons-openapi3.yml"),
        openApi
    )
}

val persons = Paths.get(System.getProperty("user.dir"), "examples", "persons-v3.yml")
    .normalize().toFile().readText()