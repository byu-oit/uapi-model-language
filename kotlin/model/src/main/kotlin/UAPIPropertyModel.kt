package edu.byu.uapi.model

sealed class UAPIPropertyModel : UAPIDocumentable, UAPIExtensible {
    abstract val name: String
    abstract val apiTypes: Set<ApiType>
    abstract val hasDescription: Boolean
    abstract val hasLongDescription: Boolean
    abstract val hasDisplayLabel: Boolean
    abstract val nullable: Boolean
}

data class UAPIValuePropertyModel(
    override val name: String,
    val key: Boolean = false,
    val value: UAPIValuePropertyType,
    override val apiTypes: Set<ApiType>,
    override val hasDescription: Boolean = false,
    override val hasLongDescription: Boolean = false,
    override val hasDisplayLabel: Boolean = false,
    override val nullable: Boolean = false,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIPropertyModel()

data class UAPIValueArrayPropertyModel(
    override val name: String,
    val valueArray: UAPIValueArrayPropertyType,
    override val apiTypes: Set<ApiType>,
    override val hasDescription: Boolean = false,
    override val hasLongDescription: Boolean = false,
    override val hasDisplayLabel: Boolean = false,
    override val nullable: Boolean = false,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIPropertyModel()

data class UAPIObjectPropertyModel(
    override val name: String,
    val `object`: UAPIObjectPropertyType,
    override val apiTypes: Set<ApiType>,
    override val hasDescription: Boolean = false,
    override val hasLongDescription: Boolean = false,
    override val hasDisplayLabel: Boolean = false,
    override val nullable: Boolean = false,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIPropertyModel()

data class UAPIObjectArrayPropertyModel(
    override val name: String,
    val objectArray: UAPIObjectArrayPropertyType,
    override val apiTypes: Set<ApiType>,
    override val hasDescription: Boolean = false,
    override val hasLongDescription: Boolean = false,
    override val hasDisplayLabel: Boolean = false,
    override val nullable: Boolean = false,
    override val documentation: String? = null,
    override val extensions: Map<String, Any> = emptyMap()
) : UAPIPropertyModel()
