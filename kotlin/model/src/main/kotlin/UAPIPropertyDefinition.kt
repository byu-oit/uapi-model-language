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
//        JsonSubTypes.Type(UAPIValuePropertyDefinition::class, name = "value"),
//        JsonSubTypes.Type(UAPIValueArrayPropertyDefinition::class, name = "value_array"),
//        JsonSubTypes.Type(UAPIObjectPropertyDefinition::class, name = "object"),
//        JsonSubTypes.Type(UAPIObjectArrayPropertyDefinition::class, name = "object_array")
//    ]
//)
sealed class UAPIPropertyDefinition : UAPICommentable, UAPIExtensible

data class UAPIValuePropertyDefinition(
    val type: UAPIValueType,
    val constraints: UAPIValueConstraints? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIPropertyDefinition()

data class UAPIValueArrayPropertyDefinition(
    val items: UAPIValuePropertyDefinition,
    val constraints: UAPIArrayConstraints? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIPropertyDefinition()

data class UAPIObjectPropertyDefinition(
    val properties: List<UAPIProperty>,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIPropertyDefinition()


data class UAPIObjectArrayPropertyDefinition(
    val items: UAPIObjectPropertyDefinition,
    val constraints: UAPIArrayConstraints? = null,
    override val `$comment`: String? = null,
    override val extensions: UAPIExtensions = mutableMapOf()
) : UAPIPropertyDefinition()
