package com.spidasoftware.schema.conversion.changeset.v5

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet

import groovy.util.logging.Log4j

@Log4j
class RemoveAdditionalPropertiesChangeset extends CalcProjectChangeSet {

    @Override
    void applyToProject(Map projectJSON) throws ConversionException {
        // Do nothing
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        removeAdditionalProperties(projectJSON, "/schema/spidacalc/calc/project-v4.schema")
    }

    @Override
    void applyToLocation(Map locationJSON) throws ConversionException {
        // Do nothing
    }

    @Override
    void revertLocation(Map locationJSON) throws ConversionException {
        removeAdditionalProperties(locationJSON, "/schema/spidacalc/calc/location-v4.schema")
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        // Do nothing
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        removeAdditionalProperties(designJSON, "/schema/spidacalc/calc/design-v4.schema")
    }

    private void removeAdditionalProperties(Map json, String v4SchemaPath) {
        removeAdditionalProperties(json, JsonLoader.fromResource(v4SchemaPath))
    }

    private static final List<String> SCHEMA_ALLOWS_ANYTHING = ["userDefinedValues"]

    // TODO: assemblies items: "anyOf
    private void removeAdditionalProperties(Map json, JsonNode schemaNode) {
        if (!SCHEMA_ALLOWS_ANYTHING.contains(schemaNode.get("id")?.asText()) && !schemaNode.get("additionalProperties")?.asBoolean()) {
            List keysToRemove = []
            json.each { key, value ->
                JsonNode schema = schemaNode.get("properties")?.get(key)

                if (schema == null) {
                    keysToRemove.add(key)
                } else {
                    if (schema.get("type")?.asText() == "object" && value instanceof Map) {
                        removeAdditionalProperties(value, schema)
                    } else if (schema.get("type")?.asText() == "array" && value instanceof List) {
                        removeAdditionalProperties((List) value, schema)
                    }
                }
            }
            keysToRemove.each { key ->
                json.remove(key)
            }
        }
    }

    private void removeAdditionalProperties(List json, JsonNode schema) {
        JsonNode items = schema.get("items")
        if (items != null) { // "components": { "type": "array" }
            if (items.isObject()) {
                if(items.get("oneOf")?.isArray() && items.get("oneOf").get(0)?.get("properties")?.get("damageType") == null) {
                    // results
                    log.info("RESULTS items = ${items.get("oneOf")}")
                    // TODO: oneOf analysis asset
                } else {
                    boolean itemOneOfAllEnum = items.get("oneOf")?.every { it.get("enum") != null }
                    // tags "oneOf": [ enum, enum ]
                    if (!itemOneOfAllEnum) {
                        json.each { val ->
                            if (val instanceof Map) {
                                removeAdditionalProperties((Map) val, items)
                            }
                        }
                    }
                }
            } else if(items.isArray()) {
                json.eachWithIndex { val, index ->
                    if (val instanceof Map && items.size() > index && items.get(index).get("type")?.asText() == "object") {
                        removeAdditionalProperties((Map) val, items.get(index))
                    }
                }
            }
        }
    }
}
