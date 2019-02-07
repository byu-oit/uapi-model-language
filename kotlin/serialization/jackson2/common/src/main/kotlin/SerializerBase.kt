package edu.byu.uapi.model.serialization.jackson2.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import edu.byu.uapi.model.serialization.UAPISerializationFormat
import edu.byu.uapi.model.serialization.UAPISerializationFormat.JSON
import edu.byu.uapi.model.serialization.UAPISerializationFormat.YAML
import serialization.UAPIReader
import serialization.UAPIWriter
import java.io.Reader
import java.io.Writer
import kotlin.reflect.KClass

abstract class SerializerBase<T : Any>(
    type: KClass<T>
) : UAPIWriter<T>, UAPIReader<T> {

    private val json = Format(ObjectMapper().init(), type)
    private val yaml = Format(YAMLMapper().init(), type)

    private fun findFormat(format: UAPISerializationFormat) = when(format) {
        JSON -> json
        YAML -> yaml
    }

    private fun ObjectMapper.init(): ObjectMapper {
        return this.also {
            setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT)
            enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
            registerKotlinModule()
            registerModule(AfterburnerModule())
            customize()
        }
    }

    abstract fun ObjectMapper.customize()

    override fun write(model: T, writer: Writer, format: UAPISerializationFormat, prettyPrint: Boolean) {
        return findFormat(format).write(model, writer, prettyPrint)
    }

    override fun read(reader: Reader, format: UAPISerializationFormat): T {
        return findFormat(format).read(reader)
    }

    private class Format<T : Any>(val mapper: ObjectMapper, val type: KClass<T>) {
        val reader: ObjectReader by lazy { mapper.readerFor(type.java) }
        val writer: ObjectWriter by lazy { mapper.writerFor(type.java) }

        val prettyWriter: ObjectWriter by lazy { writer.withDefaultPrettyPrinter() }

        fun write(model: T, writer: Writer, prettyPrint: Boolean) {
            val w = if (prettyPrint) {
                prettyWriter
            } else {
                this.writer
            }

            return w.writeValue(writer, model)
        }

        fun read(reader: Reader): T {
            return this.reader.readValue(reader)
        }
    }
}


