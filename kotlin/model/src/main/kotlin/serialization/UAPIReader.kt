package serialization

import edu.byu.uapi.model.serialization.UAPISerializationFormat
import java.io.Reader
import java.io.StringReader

interface UAPIReader<T: Any> {
    fun read(reader: Reader, format: UAPISerializationFormat = UAPISerializationFormat.JSON): T

    fun read(text: String, format: UAPISerializationFormat = UAPISerializationFormat.JSON): T {
        return read(StringReader(text), format)
    }

}
