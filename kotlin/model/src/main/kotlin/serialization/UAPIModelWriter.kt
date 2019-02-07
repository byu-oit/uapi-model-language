package edu.byu.uapi.model.serialization

import edu.byu.uapi.model.UAPIModel
import serialization.UAPIServiceLoader
import serialization.UAPIWriter

interface UAPIModelWriter: UAPIWriter<UAPIModel> {
    companion object: UAPIServiceLoader<UAPIModelWriter>(UAPIModelWriter::class)
}

