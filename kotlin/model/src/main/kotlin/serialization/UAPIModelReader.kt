package edu.byu.uapi.model.serialization

import edu.byu.uapi.model.UAPIModel
import serialization.UAPIReader
import serialization.UAPIServiceLoader

interface UAPIModelReader: UAPIReader<UAPIModel> {
    companion object: UAPIServiceLoader<UAPIModelReader>(UAPIModelReader::class)
}
