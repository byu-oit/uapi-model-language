package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import edu.byu.uapi.model.UAPIListResourceModel
import edu.byu.uapi.model.UAPIListSubresourceModel
import edu.byu.uapi.model.UAPISingletonResourceModel
import edu.byu.uapi.model.UAPISingletonSubresourceModel

@JsonTypeInfo(
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    use = JsonTypeInfo.Id.NAME
)
@JsonSubTypes(
    value = [
        JsonSubTypes.Type(UAPIListSubresourceModel::class, name = "list"),
        JsonSubTypes.Type(UAPISingletonSubresourceModel::class, name = "singleton")
    ]
)
internal interface UAPISubresourceModelMixin
