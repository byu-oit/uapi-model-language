package edu.byu.uapi.model.dialect

import com.google.auto.service.AutoService
import edu.byu.uapi.model.UAPIModel
import edu.byu.uapi.model.serialization.UAPIModelReader
import edu.byu.uapi.model.serialization.UAPIModelWriter

object UAPIDefaultDialect : UAPIDialect<UAPIModel> {

    override val name: String = "uapi"

    override val writer: UAPIModelWriter by lazy { UAPIModelWriter.getInstance() }
    override val reader: UAPIModelReader by lazy { UAPIModelReader.getInstance() }

    override fun convert(model: UAPIModel): UAPIModel = model

    @AutoService(UAPIDialect.Loader::class)
    class Loader: UAPIDialect.Loader<UAPIModel> {
        override val dialect = UAPIDefaultDialect
    }
}