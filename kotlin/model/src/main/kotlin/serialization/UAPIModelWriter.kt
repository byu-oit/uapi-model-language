package edu.byu.uapi.model.serialization

import edu.byu.uapi.model.UAPIModel

interface UAPIModelWriter: UAPIWriter<UAPIModel> {
    companion object: UAPIServiceLoader<UAPIModelWriter>(UAPIModelWriter::class)
}

