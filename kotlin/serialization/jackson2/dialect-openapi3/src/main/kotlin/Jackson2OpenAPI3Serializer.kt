package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.google.auto.service.AutoService
import edu.byu.uapi.model.UAPIModel
import edu.byu.uapi.model.openapi3.serialization.OpenAPI3Reader
import edu.byu.uapi.model.openapi3.serialization.OpenAPI3Writer
import edu.byu.uapi.model.serialization.UAPIModelReader
import edu.byu.uapi.model.serialization.UAPIModelWriter
import edu.byu.uapi.model.serialization.jackson2.common.SerializerBase
import io.swagger.v3.core.jackson.SwaggerModule
import io.swagger.v3.oas.models.OpenAPI

@AutoService(value = [OpenAPI3Writer::class, OpenAPI3Reader::class])
class Jackson2OpenAPI3Serializer : SerializerBase<OpenAPI>(OpenAPI::class), OpenAPI3Reader, OpenAPI3Writer {

    override fun ObjectMapper.customize() {
        registerModule(SwaggerModule())
    }

}

//private val start = System.currentTimeMillis()
//
//fun main() {
//    val reader = UAPIModelReader.getInstance()
//
//    val times = 1000
//
//    val first = spin(reader, persons, 1)
//    println("First load took ${first}ms")
//    val warmup = spin(reader, persons, times * 2)
//    println("warmup averaged ${warmup}ms/load")
//    val result = spin(reader, persons, times)
//    println("averaged ${result}ms/load")
//}
//
//fun spin(reader: UAPIModelReader, text: String, times: Int): BigDecimal {
//    var avg = BigDecimal.ZERO
//    val timesBd = times.toBigDecimal()
//    repeat(times) {
//        val start = System.currentTimeMillis()
//        reader.read(text, UAPISerializationFormat.YAML)
//        val end = System.currentTimeMillis()
//        avg += (end - start).toBigDecimal().divide(timesBd)
//    }
//    return avg
//}
//
//private val persons by lazy {
//    println("Reading persons yaml after ${System.currentTimeMillis() - start}")
//    val result = Paths.get(System.getProperty("user.dir"), "examples", "persons-v3.yml")
//        .normalize().toFile().readText()
//    println("Finished reading persons yaml after ${System.currentTimeMillis() - start}")
//    result
//}
