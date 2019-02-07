package edu.byu.uapi.model.openapi3

import edu.byu.uapi.model.*
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.*
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.PathParameter
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import java.math.BigDecimal
import kotlin.reflect.KClass

internal fun UAPIModel.toOpenApi3(): OpenAPI {
    return OpenAPI().also {
        it.info = info.toOpenApi3()
        resources.toOpenAPI3Paths()
            .forEach { p -> it.path(p.first, p.second) }
    }
}

internal fun UAPIInfo.toOpenApi3(): Info {
    return Info().also {
        it.title = name
        it.version = version
    }
}

internal fun Map<String, UAPIResourceModel>.toOpenAPI3Paths(): List<Pair<String, PathItem>> {
    return this.flatMap { it.value.toOpenAPI3Paths(it.key) }.sortedBy { it.first }
}

internal fun UAPIResourceModel.toOpenAPI3Paths(name: String): List<Pair<String, PathItem>> {
    return when (this) {
        is UAPIListResourceModel -> this.toOpenAPI3Paths(name)
        is UAPISingletonResourceModel -> this.toOpenAPI3Paths(name)
    } + this.getClaimPaths(name)
}

internal fun UAPIResourceModel.getClaimPaths(name: String): List<Pair<String, PathItem>> {
    val claims = this.claims.ifEmpty { return emptyList() }
    return listOf(
        "/claims/$name" to claims.toClaimPathItem(name),
        "/claims/$name/batch" to claims.toBatchClaimPathItem(name)
    )
}

internal fun Map<String, UAPIClaimModel>.toClaimPathItem(name: String): PathItem {
    return PathItem()
        .get(Operation()) //TODO
        .put(this.toClaimPutOperation(name))
}

internal fun Map<String, UAPIClaimModel>.toClaimPutOperation(name: String): Operation {
    return Operation()
        .operationId("${name}__claims")
        .requestBody(
            RequestBody()
                .content(
                    Content()
                        .addMediaType(
                            "application/json", MediaType()
                                .schema(this.toSingleClaimRequestSchema())
                        )
                )
        )
}

internal fun Map<String, UAPIClaimModel>.toSingleClaimRequestSchema(): Schema<*> {
    return ObjectSchema()
        .properties(
            mapOf(
                "subject" to StringSchema(),
                "mode" to StringSchema()._enum(listOf("ALL", "ANY", "ONE")),
                "claims" to this.toClaimsSchema()
            )
        )
}

internal fun Map<String, UAPIClaimModel>.toClaimsSchema(): Schema<*> {
//    return ObjectSchema()
//        .properties(mapOf(
//            "subject" to StringSchema(),
//            "mode" to StringSchema()._enum(listOf("ALL", "ANY", "ONE")),
//            "claims" to
//        ))
    return ArraySchema().also { a ->
        a.items = ComposedSchema()
            .oneOf(
                this.entries.map { e ->
                    val key = e.key
                    val value = e.value
                    val props = mutableMapOf<String, Schema<*>>(
                        "concept" to StringSchema().addEnumItem(key),
                        "relationship" to value.relationships.toOpenAPI3Schema(),
                        "value" to getValueSchema(value.type, value.constraints)
                    )
                    if (value.qualifiers.isNotEmpty()) {
                        props["qualifiers"] = ObjectSchema().also { qs ->
                            qs.properties = value.qualifiers.mapValues { e ->
                                val k = e.key
                                val v = e.value

                                if (v.allowMultiple) {
                                    getValueArraySchema(v.type, v.constraints)
                                } else {
                                    getValueSchema(v.type, v.constraints)
                                }.also { it.setDefault(v.default) }
                            }
                        }
                    }
                    ObjectSchema()
                        .properties(props)
                }
            )
    }
}

internal fun Map<String, UAPIClaimModel>.toBatchClaimPathItem(name: String): PathItem {
    return PathItem()
        .put(Operation()) //TODO
}

internal fun UAPIListResourceModel.toOpenAPI3Paths(name: String): List<Pair<String, PathItem>> {
    val parent = asSubresourceParent(name)
    return listOf(
        "/$name" to this.listPathItem(name),
        parent.path to this.singlePathItem(name, parent.params)
    ) + this.subresources.flatMap {
        it.value.toOpenAPI3Paths(parent, it.key).toList()
    }
}

internal fun UAPIListResourceModel.asSubresourceParent(name: String): SubresourceParent {
    val (idPath, idParams) = this.getIdPath()
    return SubresourceParent(name, "/$name/$idPath", idParams, keys)
}

private fun UAPISubresourceModel.toOpenAPI3Paths(
    parent: SubresourceParent,
    name: String
): Map<String, PathItem> {
    return when (this) {
        is UAPIListSubresourceModel -> this.toOpenAPI3Paths(parent, name)
        is UAPISingletonSubresourceModel -> this.toOpenAPI3Paths(parent, name)
    }
}

internal fun UAPIListSubresourceModel.toOpenAPI3Paths(
    parent: SubresourceParent,
    name: String
): Map<String, PathItem> {
    val (idPath, idParams) = this.getIdPath()
    val (_, parentPath, _) = parent
    return mapOf(
        "$parentPath/$name" to getListPathItem(parent, name),
        "$parentPath/$name/$idPath" to getSinglePathItem(parent, idParams, name)
    )
}

internal fun UAPISingletonSubresourceModel.toOpenAPI3Paths(
    parent: SubresourceParent,
    name: String
): Map<String, PathItem> {
    return mapOf(
        "${parent.path}/$name" to toPathItem(parent, name)
    )
}


internal fun UAPIListResourceModel.listPathItem(name: String): PathItem {
    return PathItem().also {
        it.description = this.documentation
        it.get = this.toListOperation(name)
        it.post = this.create?.let { c -> Operation() }
    }
}

data class SubresourceParent(
    val name: String,
    val path: String,
    val params: List<PathParameter>,
    val keys: Collection<String>
)

internal fun UAPIListSubresourceModel.getListPathItem(parent: SubresourceParent, name: String): PathItem {
    val sr = this
    return PathItem().apply {
        description = sr.documentation
        summary = "Operations on $name subresource collection"
        parameters = parent.params
        get = sr.toListOperation(parent, name)
        post = sr.create?.let { c -> Operation() }
    }
}

internal fun UAPIListSubresourceModel.getSinglePathItem(
    parent: SubresourceParent,
    idParams: List<PathParameter>,
    name: String
): PathItem {
    val sr = this
    return PathItem().apply {
        description = sr.documentation
        summary = "Operations on $name subresource"
        parameters = parent.params + idParams
        get = sr.toSingleGetOperation(parent, name)
        put = sr.update?.let { c -> Operation() }
        delete = sr.delete?.run { toDeleteOperation(name) }
    }
}

internal fun UAPISingletonSubresourceModel.toPathItem(
    parent: SubresourceParent,
    name: String
): PathItem {
    val sr = this
    return PathItem().apply {
        summary = "Operations on $name"
        description = sr.documentation
        parameters = parent.params
        get = sr.toGetOperation(parent, name)
        put = sr.update?.let { c -> Operation() }
        delete = sr.delete?.run { toDeleteOperation(name) }
    }
}

internal fun UAPIListResourceModel.getIdPath(): Pair<String, List<PathParameter>> {
    val path = this.keys.joinToString(prefix = "{", separator = "},{", postfix = "}")
    val params = this.keys.map { name ->
        PathParameter().also { p ->
            p.name = name
            val prop = this.properties.getValue(name)
            p.description = prop.documentation
            p.required = true
            val defn = prop.type
            if (defn !is UAPIValuePropertyTypeModel) {
                throw IllegalStateException("Key properties must be simple value types. '$name' isn't.")
            }
            p.schema = getValueSchema(defn.type, defn.constraints)
        }
    }
    return path to params
}

internal fun UAPIListSubresourceModel.getIdPath(): Pair<String, List<PathParameter>> {
    val path = this.keys.joinToString(prefix = "{", separator = "},{", postfix = "}")
    val params = this.keys.map { name ->
        PathParameter().also { p ->
            p.name = name
            val prop = this.properties.getValue(name)
            p.description = prop.documentation
            p.required = true
            val defn = prop.type
            if (defn !is UAPIValuePropertyTypeModel) {
                throw IllegalStateException("Key properties must be simple value types. '$name' isn't.")
            }
            p.schema = getValueSchema(defn.type, defn.constraints)
        }
    }
    return path to params
}

internal fun UAPIListResourceModel.singlePathItem(resourceName: String, idParams: List<PathParameter>): PathItem {
    return PathItem().also {
        it.parameters = idParams
        it.description = this.documentation
        it.get = this.toSingleGetOperation(resourceName)
        it.put = this.update?.let { u -> Operation() }
        it.delete = this.delete?.run { toDeleteOperation(resourceName) }
    }
}

internal fun UAPIDeleteMutation.toDeleteOperation(name: String): Operation {
    return Operation().also { o ->
        o.operationId = "${name}__delete"
        o.summary = "Delete $name"
        o.description = this.documentation
        o.responses = deleteResponses()

    }
}

internal fun deleteResponses(): ApiResponses {
    return ApiResponses().also { r ->
        r["204"] = ApiResponse().description("Success")
        r["403"] = errorResponse("Caller is not allowed to delete this record.", 403)
        r["409"] = errorResponse(
            "Unable To Delete\n\nBusiness rules prevent this record from being deleted in its current state.",
            409
        )
        r.default = errorResponse("Generic Error")
    }
}

internal fun UAPIListResourceModel.toListOperation(name: String): Operation {
    return Operation().also { op ->
        op.summary = "Gets a list of $name"
        op.description = this.list?.documentation
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
                                this.listItemModel(this.asSubresourceParent(name)),
                                subresources.keys + "basic"
                            )
                        )
                    )
                )
            r.default = uapiErrorResponse
        }
    }
}


internal fun UAPIListSubresourceModel.toListOperation(parent: SubresourceParent, name: String): Operation {
    val sr = this
    val list = sr.list
    return Operation().apply {
        summary = "List $name"
        operationId = "${parent.name}__${name}__list"
        description = sr.list?.documentation
        parameters = sr.getListParameters(name)
        extensions = sr.extensions.ifEmpty { null }
        responses = ApiResponses().apply {
            this["200"] = ApiResponse()
                .content(
                    Content().addMediaType(
                        "application/json",
                        MediaType().schema(
                            sr.listSchemaFor(sr.toResponseItemSchema(parent))
                        )
                    )
                )
            default = uapiErrorResponse
        }
    }
}

internal fun UAPIListResourceModel.toSingleGetOperation(name: String): Operation {
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
                            res.listItemModel(res.asSubresourceParent(name))
                        )
                    )
                )
            default = uapiErrorResponse
        }
    }
}

internal fun UAPIListSubresourceModel.toSingleGetOperation(parent: SubresourceParent, name: String): Operation {
    val singleName = when {
        singularName != null -> singularName
        name.endsWith("s") -> name.substring(0, name.length - 1)
        else -> name
    }
    val sr = this
    return Operation().apply {
        summary = "Get single $singleName"
        operationId = "${parent.name}__${singleName}__info"
        extensions = sr.extensions.ifEmpty { null }
        responses = ApiResponses().apply {
            this["200"] = ApiResponse()
                .content(
                    Content().addMediaType(
                        "application/json",
                        MediaType().schema(
                            sr.toResponseItemSchema(parent)
                        )
                    )
                )
            default = uapiErrorResponse
        }
    }
}

internal fun UAPISingletonSubresourceModel.toGetOperation(
    parent: SubresourceParent,
    name: String
): Operation {
    val sr = this
    return Operation().apply {
        summary = "Get $name"
        operationId = "${parent.name}__${name}__info"
        extensions = sr.extensions.ifEmpty { null }
        responses = ApiResponses().apply {
            this["200"] = ApiResponse()
                .content(
                    Content().addMediaType(
                        "application/json",
                        MediaType().schema(
                            sr.toResponseItemSchema(parent)
                        )
                    )
                )
        }
    }
}

internal fun UAPIListResourceModel.listItemModel(parent: SubresourceParent): ObjectSchema {
    val res = this
    return ObjectSchema().apply {
        additionalProperties = false
        properties = linkedMapOf(
            "basic" to res.toResponseItemSchema()
        ) + res.subresources
            .mapValues { it.value.toResponseItemSchema(parent) }
            .toSortedMap()
    }
}

internal fun UAPIResourceModel.toResponseItemSchema(): Schema<*> {
    return when (this) {
        is UAPIListResourceModel -> this.toResponseItemSchema()
        is UAPISingletonResourceModel -> this.toResponseItemSchema()
    }
}

internal fun UAPIListResourceModel.toResponseItemSchema(): Schema<*> {
    return ObjectSchema().properties(this.properties.toSchema(this.keys))
}

internal fun UAPISingletonResourceModel.toResponseItemSchema(): Schema<*> {
    return ObjectSchema().properties(this.properties.toSchema())
}

internal fun UAPISubresourceModel.toResponseItemSchema(parent: SubresourceParent): ObjectSchema {
    return when (this) {
        is UAPIListSubresourceModel -> this.toResponseItemSchema(parent)
        is UAPISingletonSubresourceModel -> this.toResponseItemSchema(parent)
    }
}

internal fun UAPIListSubresourceModel.toResponseItemSchema(parent: SubresourceParent): ObjectSchema {
    return ObjectSchema().also { it.properties(properties.toSchema(keys + parent.keys)) }
}

internal fun UAPISingletonSubresourceModel.toResponseItemSchema(parent: SubresourceParent): ObjectSchema {
    return ObjectSchema().also { it.properties(properties.toSchema(parent.keys)) }
}

internal fun Map<String, UAPIPropertyModel>.toSchema(keys: Collection<String> = emptySet()): Map<String, Schema<*>> {
    return this.mapValues { it.value.toSchema(it.key in keys) }
        .toSortedMap()
}

private fun UAPIPropertyModel.toSchema(isKey: Boolean): Schema<*> {
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

    type.toNameAndSchema().apply {
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

internal fun UAPIPropertyTypeModel.toNameAndSchema(): Pair<String, Schema<*>> {
    return when (this) {
        is UAPIValuePropertyTypeModel -> "value" to this.toSchema()
        is UAPIValueArrayPropertyTypeModel -> "value_array" to this.toSchema()
        is UAPIObjectPropertyTypeModel -> "object" to this.toSchema()
        is UAPIObjectArrayPropertyTypeModel -> "object_array" to this.toSchema()
    }
}

internal fun UAPIValuePropertyTypeModel.toSchema(): Schema<*> {
    return getValueSchema(this.type, this.constraints)
}

internal fun UAPIValueArrayPropertyTypeModel.toSchema(): Schema<*> {
    return getValueArraySchema(this.items.type, this.items.constraints, this.constraints)
}

internal fun UAPIObjectPropertyTypeModel.toSchema(): Schema<*> {
    return ObjectSchema().properties(this.properties.toSchema())
}

internal fun UAPIObjectArrayPropertyTypeModel.toSchema(): Schema<*> {
    return ArraySchema().also { a ->
        a.items = this.items.toSchema()
    }
}

internal fun UAPIHasListFeature.listSchemaFor(
    itemSchema: ObjectSchema,
    fieldsets: Set<String> = emptySet()
): ObjectSchema {
    return ObjectSchema().also { root ->
        root.properties = mapOf(
            // TODO: Better Links
            "links" to ObjectSchema(),
            "metadata" to listMetadata(fieldsets),
            "values" to ArraySchema().also { values ->
                values.default = emptyList<Any>()
                values.items = itemSchema
            }
        )
    }
}

private fun UAPIHasListFeature.listMetadata(fieldsets: Set<String> = emptySet()): ObjectSchema {
    val list = this.list
    val meta = mutableMapOf<String, Schema<*>>()
    val req = mutableListOf<String>()

    meta["collection_size"] = IntegerSchema()

    list?.sorting.ifNotNull { s ->
        meta["sort_properties_available"] =
            ArraySchema().also { a ->
                a.items = StringSchema()._enum(s.availableSortProperties)
                a.uniqueItems = true
                a.default = s.availableSortProperties
            }
        meta["sort_properties_default"] =
            ArraySchema().also { a ->
                a.items = StringSchema()._enum(s.availableSortProperties)
                a.uniqueItems = true
                a.default = s.defaultSortProperties
            }
        meta["sort_order_default"] = UAPISortOrder.values().toOpenAPI3Schema()
            ._default(s.defaultSortOrder.apiValue)

        req += listOf("sort_properties_available", "sort_properties_default", "sort_order_default")
    }
    list?.subset.ifNotNull { s ->
        meta["default_subset_size"] = IntegerSchema()._default(s.defaultSize)
        meta["max_subset_size"] = IntegerSchema()._default(s.maxSize)
        meta["subset_start"] = IntegerSchema().minimum(BigDecimal.ZERO)
        meta["subset_size"] = IntegerSchema().minimum(BigDecimal.ZERO).maximum(s.maxSize.toBigDecimal())

        req += listOf("default_subset_size", "max_subset_size", "subset_start", "subset_size")
    }
    list?.search.ifNotNull { s ->
        meta["search_contexts_available"] = ObjectSchema().apply {
            properties = s.searchContextsAvailable.mapValues {
                val v = it.value
                ArraySchema().apply {
                    items = StringSchema()
                    default = v
                } as Schema<*>
            }.toSortedMap()
            required = s.searchContextsAvailable.keys.sorted()
        }

        req += listOf("search_contexts_available")
    }

    if (fieldsets.isNotEmpty()) {
        val fl = fieldsets.toList().sorted()
        val itemSchema = StringSchema()._enum(fl)
        meta["field_sets_returned"] = ArraySchema().apply {
            items = itemSchema
            uniqueItems = true
        }
        meta["field_sets_available"] = ArraySchema().apply {
            items = itemSchema
            uniqueItems = true
            default = fieldsets
        }
        meta["field_sets_default"] = ArraySchema().apply {
            items = itemSchema
            uniqueItems = true
            default = listOf("basic")
        }

        req += listOf("field_sets_returned", "field_sets_available", "field_sets_default")
    }

    return metadataSchema(meta)
}

private fun UAPIListResourceModel.getListParameters(name: String): List<Parameter> {
    return (list?.getListParameters().orEmpty() + this.getFieldsetParameters())
}

private fun UAPIListSubresourceModel.getListParameters(name: String): List<Parameter> {
    return list?.getListParameters().orEmpty()
}

internal fun UAPIListFeatureModel.getListParameters(): List<Parameter> {
    return filters.getListParameters() +
            sorting?.getListParameters().orEmpty() +
            subset?.getListParameters().orEmpty() +
            search?.getListParameters().orEmpty()
}

internal fun UAPIListFiltersFeature.getListParameters(): List<Parameter> {
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

internal fun UAPIListSortFeature.getListParameters(): List<Parameter> {
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

internal fun UAPIListSubsetFeature.getListParameters(): List<Parameter> {
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

internal fun UAPIListSearchFeature.getListParameters(): List<Parameter> {
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

internal fun UAPIResourceModel.getFieldsetParameters(): List<Parameter> {
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

internal fun UAPISingletonResourceModel.toOpenAPI3Paths(name: String): List<Pair<String, PathItem>> {
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
