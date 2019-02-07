package edu.byu.uapi.model.serialization

import edu.byu.uapi.model.UAPIModel

interface UAPIModelReader: UAPIReader<UAPIModel> {
    companion object: UAPIServiceLoader<UAPIModelReader>(UAPIModelReader::class)
}
