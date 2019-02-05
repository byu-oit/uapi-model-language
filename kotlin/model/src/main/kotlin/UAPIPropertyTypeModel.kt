package edu.byu.uapi.model

//import com.fasterxml.jackson.annotation.JsonSubTypes
//import com.fasterxml.jackson.annotation.JsonTypeInfo
//
//@JsonTypeInfo(
//    use = JsonTypeInfo.Id.NAME,
//    include = JsonTypeInfo.As.WRAPPER_OBJECT,
//    property = "foo"
//)
//@JsonSubTypes(
//    value = [
//        JsonSubTypes.Type(UAPIValuePropertyTypeModel::class, name = "value"),
//        JsonSubTypes.Type(UAPIValueArrayPropertyTypeModel::class, name = "value_array"),
//        JsonSubTypes.Type(UAPIObjectPropertyTypeModel::class, name = "object"),
//        JsonSubTypes.Type(UAPIObjectArrayPropertyTypeModel::class, name = "object_array")
//    ]
//)
sealed class UAPIPropertyTypeModel : UAPICommentable, UAPIExtensible

data class UAPIValuePropertyTypeModel(
    val type: UAPIValueType,
    val constraints: UAPIValueConstraints? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIPropertyTypeModel()

data class UAPIValueArrayPropertyTypeModel(
    val items: UAPIValuePropertyTypeModel,
    val constraints: UAPIArrayConstraints? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIPropertyTypeModel()

data class UAPIObjectPropertyTypeModel(
    val properties: Map<String, UAPIPropertyModel>,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIPropertyTypeModel()


data class UAPIObjectArrayPropertyTypeModel(
    val items: UAPIObjectPropertyTypeModel,
    val constraints: UAPIArrayConstraints? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIPropertyTypeModel()
