package edu.byu.uapi.model.openapi3

import edu.byu.uapi.model.*
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.*
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.PathParameter
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import java.math.BigDecimal
import kotlin.reflect.KClass

fun UAPIModel.toOpenApi3(): OpenAPI {
    return OpenAPI().also {
        it.info = info.toOpenApi3()
        resources.toOpenAPI3Paths()
            .forEach { p -> it.path(p.first, p.second) }
    }
}

fun UAPIInfo.toOpenApi3(): Info {
    return Info().also {
        it.title = name
        it.version = version
    }
}

fun Map<String, UAPIResourceModel>.toOpenAPI3Paths(): List<Pair<String, PathItem>> {
    return this.flatMap { it.value.toOpenAPI3Paths(it.key) }.sortedBy { it.first }
}

fun UAPIResourceModel.toOpenAPI3Paths(name: String): List<Pair<String, PathItem>> {
    return when (this) {
        is UAPIListResourceModel -> this.toOpenAPI3Paths(name)
        is UAPISingletonResourceModel -> this.toOpenAPI3Paths(name)
    }
}

fun UAPIListResourceModel.toOpenAPI3Paths(name: String): List<Pair<String, PathItem>> {
    val (idPath, idParams) = this.getIdPath()
    val singlePath = "/$name/$idPath"
    return listOf(
        "/$name" to this.listPathItem(name),
        singlePath to this.singlePathItem(name, idParams)
    ) + this.subresources.flatMap { it.value.toOpenAPI3Paths(singlePath, idParams, it.key).toList() }
}

private fun UAPISubresourceModel.toOpenAPI3Paths(
    rootPath: String,
    idParams: List<PathParameter>,
    key: String
): Map<String, PathItem> {
    return when (this) {
        is UAPIListSubresourceModel -> this.toOpenAPI3Paths(rootPath, idParams, key)
        is UAPISingletonSubresourceModel -> this.toOpenAPI3Paths(rootPath, idParams, key)
    }
}

fun UAPIListSubresourceModel.toOpenAPI3Paths(
    rootPath: String,
    rootIdParams: List<PathParameter>,
    name: String
): Map<String, PathItem> {
    val (idPath, idParams) = this.getIdPath()
    return mapOf(
        "$rootPath/$name" to PathItem(),
        "$rootPath/$idPath" to PathItem()
    )
}

fun UAPISingletonSubresourceModel.toOpenAPI3Paths(
    rootPath: String,
    rootIdParams: List<PathParameter>,
    name: String
): Map<String, PathItem> {
    return mapOf(
        "$rootPath/$name" to PathItem()
    )
}

fun UAPIListResourceModel.listPathItem(name: String): PathItem {
    return PathItem().also {
        it.description = this.documentation
        it.get = this.toListOperation(name)
        it.post = this.create?.let { c -> Operation() }
    }
}

fun UAPIListResourceModel.getIdPath(): Pair<String, List<PathParameter>> {
    val path = this.keys.joinToString(prefix = "{", separator = "},{", postfix = "}")
    val params = this.keys.map { name ->
        PathParameter().also { p ->
            p.name = name
            val prop = this.properties.getValue(name)
            p.description = prop.documentation
            p.required = true
            val defn = prop.definition
            if (defn !is UAPIValuePropertyDefinition) {
                throw IllegalStateException("Key properties must be simple value types. '$name' isn't.")
            }
            p.schema = getValueSchema(defn.type, defn.constraints)
        }
    }
    return path to params
}

fun UAPIListSubresourceModel.getIdPath(): Pair<String, List<PathParameter>> {
    val path = this.keys.joinToString(prefix = "{", separator = "},{", postfix = "}")
    val params = this.keys.map { name ->
        PathParameter().also { p ->
            p.name = name
            val prop = this.properties.getValue(name)
            p.description = prop.documentation
            p.required = true
            val defn = prop.definition
            if (defn !is UAPIValuePropertyDefinition) {
                throw IllegalStateException("Key properties must be simple value types. '$name' isn't.")
            }
            p.schema = getValueSchema(defn.type, defn.constraints)
        }
    }
    return path to params
}

fun UAPIListResourceModel.singlePathItem(resourceName: String, idParams: List<PathParameter>): PathItem {
    return PathItem().also {
        it.parameters = idParams
        it.description = this.documentation
        it.get = this.toSingleGetOperation(resourceName)
        it.put = this.update?.let { u -> Operation() }
        it.delete = this.delete?.let { d -> Operation() }
    }
}

fun UAPIListResourceModel.toListOperation(name: String): Operation {
    return Operation().also { op ->
        op.summary = "Gets a list of $name"
        op.description = documentation
        op.operationId = "${name}__list"
        op.parameters = this.getListParameters(name)
        op.extensions = this.extensions.ifEmpty { null }
        op.responses = ApiResponses().also { r ->
            r["200"] = ApiResponse()
                .content(
                    Content().addMediaType(
                        "application/json",
                        MediaType().schema(
                            listSchemaFor(
                                this.listItemModel()
                            )
                        )
                    )
                )
            r.default = uapiErrorResponse
        }
    }
}

fun UAPIListResourceModel.toSingleGetOperation(name: String): Operation {
    val singleName = when {
        singularName != null -> singularName
        name.endsWith("s") -> name.substring(0, name.length - 1)
        else -> name
    }
    val res = this
    return Operation().apply {
        summary = "Gets a single $singleName"
        description = res.documentation
        operationId = "${singleName}__info"
        parameters = res.getFieldsetParameters()
        extensions = res.extensions.ifEmpty { null }
        responses = ApiResponses().apply {
            this["200"] = ApiResponse()
                .content(
                    Content().addMediaType(
                        "application/json",
                        MediaType().schema(
                            res.listItemModel()
                        )
                    )
                )
            default = uapiErrorResponse
        }
    }
}

fun UAPIListResourceModel.listItemModel(): ObjectSchema {
    val res = this
    return ObjectSchema().apply {
        additionalProperties = false
        properties = linkedMapOf(
            "basic" to res.toResponseItemSchema()
        ) + res.subresources.mapValues { it.value.toResponseItemSchema() }.toSortedMap()
    }
}

fun UAPIResourceModel.toResponseItemSchema(): Schema<*> {
    return when (this) {
        is UAPIListResourceModel -> this.toResponseItemSchema()
        is UAPISingletonResourceModel -> this.toResponseItemSchema()
    }
}

fun UAPIListResourceModel.toResponseItemSchema(): Schema<*> {
    return ObjectSchema().properties(this.properties.toSchema(this.keys))
}

fun UAPISingletonResourceModel.toResponseItemSchema(): Schema<*> {
    return ObjectSchema().properties(this.properties.toSchema())
}

fun UAPISubresourceModel.toResponseItemSchema(): Schema<*> {
    return when (this) {
        is UAPIListSubresourceModel -> this.toResponseItemSchema()
        is UAPISingletonSubresourceModel -> this.toResponseItemSchema()
    }
}

fun UAPIListSubresourceModel.toResponseItemSchema(): Schema<*> {
    return ObjectSchema().properties(properties.toSchema(keys))
}

fun UAPISingletonSubresourceModel.toResponseItemSchema(): Schema<*> {
    return ObjectSchema().properties(properties.toSchema())
}

fun Map<String, UAPIProperty>.toSchema(keys: Collection<String> = emptySet()): Map<String, Schema<*>> {
    return this.mapValues { it.value.toSchema(it.key in keys) }
        .toSortedMap()
}

private fun UAPIProperty.toSchema(isKey: Boolean): Schema<*> {
    val props = mutableMapOf<String, Schema<*>>(
        "api_type" to this.apiTypes.toOpenAPI3Schema()
    )

    if (isKey) {
        props["key"] = BooleanSchema().addEnumItem(true)
    }

    if (hasDescription) {
        props["description"] = StringSchema()
            .description("Explains the data value in human-friendly terms. Should be limited to 30 characters.")
            .nullable(true)
            .maxLength(30)
    }

    if (hasLongDescription) {
        props["long_description"] = StringSchema()
            .description("Explains the data value in human-friendly terms; contains more information than the (short) description. Should be limited to 256 characters.")
            .nullable(true)
            .maxLength(256)
    }

    if (hasDisplayLabel) {
        props["long_description"] = StringSchema()
            .description("Provides a suggested string to use when creating a label for this property in the user interface. Should be limited to 30 characters.")
            .nullable(true)
            .maxLength(30)
    }

    definition.toNameAndSchema().apply {
        props[first] = second
    }

    //TODO: Domains
    if (UAPIApiType.RELATED in apiTypes) {
        props["related_resources"] = StringSchema()
            .format("uri")
    }

    return ObjectSchema().also { s ->
        s.description = this.documentation
        s.properties = props
        s.additionalProperties = true

        if (UAPIApiType.MODIFIABLE !in this.apiTypes) {
            s.readOnly = true
        }
    }
}

fun UAPIPropertyDefinition.toNameAndSchema(): Pair<String, Schema<*>> {
    return when (this) {
        is UAPIValuePropertyDefinition -> "value" to this.toSchema()
        is UAPIValueArrayPropertyDefinition -> "value_array" to this.toSchema()
        is UAPIObjectPropertyDefinition -> "object" to this.toSchema()
        is UAPIObjectArrayPropertyDefinition -> "object_array" to this.toSchema()
    }
}

fun UAPIValuePropertyDefinition.toSchema(): Schema<*> {
    return getValueSchema(this.type, this.constraints)
}

fun UAPIValueArrayPropertyDefinition.toSchema(): Schema<*> {
    return getValueArraySchema(this.items.type, this.items.constraints, this.constraints)
}

fun UAPIObjectPropertyDefinition.toSchema(): Schema<*> {
    return ObjectSchema().properties(this.properties.toSchema())
}

fun UAPIObjectArrayPropertyDefinition.toSchema(): Schema<*> {
    return ArraySchema().also { a ->
        a.items = this.items.toSchema()
    }
}

fun listSchemaFor(
    itemSchema: ObjectSchema
): ObjectSchema {
    return ObjectSchema().also { root ->
        root.properties = mapOf(
            // TODO: Better Links
            "links" to ObjectSchema(),
            "metadata" to ObjectSchema(),
            "values" to ArraySchema().also { values ->
                values.default = emptyList<Any>()
                values.items = itemSchema
            }
        )
    }
}

private fun UAPIListResourceModel.getListParameters(name: String): List<Parameter> {
    return (list?.getListParameters().orEmpty() + this.getFieldsetParameters())
}

fun UAPIListFeatureModel.getListParameters(): List<Parameter> {
    return filters.getListParameters() +
            sorting?.getListParameters().orEmpty() +
            subset?.getListParameters().orEmpty() +
            search?.getListParameters().orEmpty()
}

fun UAPIListFiltersFeature.getListParameters(): List<Parameter> {
    return this.map {
        val (key, param) = it
        Parameter().apply {
            name = key
            `in` = "query"
            required = false
            if (param.allowMultiple) {
                schema = getValueArraySchema(param.type, param.constraints)
                style = Parameter.StyleEnum.SIMPLE
            } else {
                val value = getValueSchema(param.type, param.constraints)
                val ops = param.supportedOperators
                if (ops.isNullOrEmpty()) {
                    schema = value
                } else {
                    schema = ComposedSchema().oneOf(
                        listOf(
                            value,
                            ops.toObjectSchema(value)
                        )
                    )
                }
            }
        }
    }
}

fun UAPIListSortFeature.getListParameters(): List<Parameter> {
    val sortProperty = Parameter().also { p ->
        p.name = "sort_properties"
        p.`in` = "query"
        p.required = false
        p.schema = ArraySchema().also { a ->
            a.uniqueItems = true
            a.items = StringSchema().also { s -> s.enum = this.availableSortProperties.toList() }
            a.default = this.defaultSortProperties
        }
        p.style = Parameter.StyleEnum.SIMPLE
    }
    val sortOrder = Parameter().also { p ->
        p.name = "sort_order"
        p.`in` = "query"
        p.required = false
        p.schema = UAPISortOrder.values().toOpenAPI3Schema()
    }
    return listOf(sortProperty, sortOrder)
}

fun UAPIListSubsetFeature.getListParameters(): List<Parameter> {
    val startOffset = Parameter().also { p ->
        p.name = "subset_start_offset"
        p.`in` = "query"
        p.required = false
        p.description = "The offset for the first resource in the collection to be returned in this subset."
        p.schema = IntegerSchema().also { s ->
            s.minimum = BigDecimal.ZERO
            s.format = "int64"
            s._default(BigDecimal.ZERO)
        }
    }
    val size = Parameter().also { p ->
        p.name = "subset_size"
        p.`in` = "query"
        p.required = false
        p.description = "The number of resources in the collection to return."
        p.schema = IntegerSchema().also { s ->
            s.minimum = BigDecimal.ONE
            s.maximum = maxSize.toBigDecimal()
            s.format = "int32"
            s._default(defaultSize.toBigDecimal())
        }
    }
    return listOf(startOffset, size)
}

fun UAPIListSearchFeature.getListParameters(): List<Parameter> {
    val context = Parameter().also { p ->
        p.name = "search_context"
        p.`in` = "query"
        p.required = false

        val descriptionLines = listOf(
            "The search context to use.",
            "",
            "Available contexts and the properties they search:"
        ) + searchContextsAvailable.flatMap { c ->
            listOf(
                "* ${c.key}"
            ) + c.value.map { "  * $it" }
        }
        p.description = descriptionLines.joinToString("\n")

        p.schema = StringSchema().also { s ->
            s.enum = this.searchContextsAvailable.keys.toList()
        }
    }
    val text = Parameter().also { p ->
        p.name = "search_text"
        p.`in` = "query"
        p.required = false
        p.description = "The text to search for in the specified context"
        p.schema = StringSchema()
    }
    return listOf(context, text)
}

fun UAPIResourceModel.getFieldsetParameters(): List<Parameter> {
    val keys = listOf("basic") + this.subresources.keys.sorted()
    return listOf(Parameter().apply {
        name = "field_sets"
        `in` = "query"
        required = false
        description = "Which sub-resources are to be included in the response."
        schema = ArraySchema().also { s ->
            s.default = listOf("basic")
            s.items = StringSchema()._enum(keys)
            s.uniqueItems = true
        }
        style = Parameter.StyleEnum.SIMPLE
    })
    // TODO: Contexts
}

fun UAPISingletonResourceModel.toOpenAPI3Paths(name: String): List<Pair<String, PathItem>> {
    return emptyList()
}

interface DefinitionCollector {
    fun define(name: String, value: Schema<*>)

    fun define(forType: KClass<*>, value: Schema<*>) {
        define(forType.simpleName!!, value)
    }

    fun define(forType: KClass<*>, qualifier: String, value: Schema<*>) {
        define("${forType.simpleName!!}_$qualifier", value)
    }
}
