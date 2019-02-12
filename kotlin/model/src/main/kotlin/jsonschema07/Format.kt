package edu.byu.uapi.model.jsonschema07

sealed class Format(open val value: String) {
    object DateTime : Format("date-time")
    object Date : Format("date")
    object Time : Format("time")
    object Email : Format("email")
    object IdnEmail : Format("idn-email")
    object Hostname : Format("hostname")
    object IdnHostname : Format("idn-hostname")
    object IpV4 : Format("ipv4")
    object IpV6 : Format("ipv6")
    object Uri : Format("uri")
    object UriReference : Format("uri-reference")
    object Iri : Format("iri")
    object IriReference : Format("iri-reference")
    object UriTemplate : Format("uri-template")
    object JsonPointer : Format("json-pointer")
    object RelativeJsonPointer : Format("relative-json-pointer")
    object Regex : Format("regex")

    data class Other(override val value: String) : Format(value) {
        override fun toString(): String {
            return super.toString()
        }
    }

    override fun toString(): String {
        return "Format($value)"
    }
}