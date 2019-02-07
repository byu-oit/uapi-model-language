package edu.byu.uapi.model.dialect

import edu.byu.uapi.model.UAPIModel
import edu.byu.uapi.model.serialization.UAPIReader
import edu.byu.uapi.model.serialization.UAPISerializationFormat
import edu.byu.uapi.model.serialization.UAPIWriter
import java.io.Writer
import java.util.*

interface UAPIDialect<Output : Any> {
    val name: String

    val writer: UAPIWriter<Output>
    val reader: UAPIReader<Output>

    fun convert(model: UAPIModel): Output

    fun convertAndWrite(
        model: UAPIModel,
        writer: Writer,
        format: UAPISerializationFormat = UAPISerializationFormat.JSON,
        prettyPrint: Boolean = false
    ) {
        this.writer.write(
            model = convert(model),
            writer = writer,
            format = format,
            prettyPrint = prettyPrint
        )
    }

    companion object {
        private val loader: ServiceLoader<Loader<*>> by lazy { ServiceLoader.load(Loader::class.java) }
        fun findAllDialects(forceReload: Boolean = false): List<UAPIDialect<*>> {
            if (forceReload) {
                loader.reload()
            }
            return loader.map { it.dialect }
        }
    }

    interface Loader<T: Any> {
        val dialect: UAPIDialect<T>
    }
}

