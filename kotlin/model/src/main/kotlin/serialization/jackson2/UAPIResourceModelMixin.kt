package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import edu.byu.uapi.model.UAPIListResourceModel
import edu.byu.uapi.model.UAPISingletonResourceModel

@JsonTypeInfo(
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    use = JsonTypeInfo.Id.NAME
)
@JsonSubTypes(
    value = [
        JsonSubTypes.Type(UAPIListResourceModel::class, name = "list"),
        JsonSubTypes.Type(UAPISingletonResourceModel::class, name = "singleton")
    ]
)
interface UAPIResourceModelMixin
