package com.spidasoftware.schema.conversion.changeset.v5

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet

import groovy.util.logging.Log4j

@Log4j
class RemoveAdditionalPropertiesChangeset extends CalcProjectChangeSet {
    // TODO: crossAreaPercent
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

    // These fields can have any value they want, these won't be checked
    private static final List<String> SCHEMA_ALLOWS_ANYTHING = ["userDefinedValues"]
    // structure.sidewalkBraces doenn't have a type in the 7.0 schema, it is an array, it also doesn't have an id, use the description to identify.
    private static final List<String> DESCRIPTION_ARRAY_TYPE_OVERRIDES = ["List of all sidewalk braces (AKA queen posts)"]

    // TODO: assemblies items: "anyOf
    private void removeAdditionalProperties(Map json, JsonNode schemaNode) {
        if (!SCHEMA_ALLOWS_ANYTHING.contains(schemaNode.get("id")?.asText()) && !schemaNode.get("additionalProperties")?.asBoolean()) {
            List keysToRemove = []
            json.each { key, value ->
                JsonNode schema = schemaNode.get("properties")?.get(key)

                if (schema == null) {
                    keysToRemove.add(key)
                } else {
                    boolean isObject = schema.get("type")?.asText() == "object"
                    boolean valueIsMap = (value instanceof Map)
                    boolean isArray = (schema.get("type")?.asText() == "array" || DESCRIPTION_ARRAY_TYPE_OVERRIDES.contains(schema.get("description")?.asText()))
                    boolean valueIsList = (value instanceof List)

                    if (isObject && valueIsMap) {
                        removeAdditionalProperties((Map) value, schema)
                    } else if(isArray && valueIsList) {
                        removeAdditionalProperties((List) value, schema)
                    }
                }
            }
            log.trace("removing ${keysToRemove} from ${json}")
            keysToRemove.each { key ->
                json.remove(key)
            }
        }

    }

    private void removeAdditionalProperties(List json, JsonNode schema) {
        JsonNode items = schema.get("items")
        if (items != null) { // "components": { "type": "array" }
            if (items.isObject()) {
                boolean isArray = items.get("oneOf")?.isArray()
                boolean isResult = items.get("oneOf")?.any { it.get("id")?.asText() == "#/spidacalc/results/result.schema" }

                if(isArray && isResult) {
                    JsonNode summarySchema = items.get("oneOf").find { it.get("id").asText() == "#/spidamin/asset/standard_details/analysis_asset.schema" }
                    JsonNode detailedSchema = items.get("oneOf").find { it.get("id").asText() == "#/spidacalc/results/result.schema" }

                    json.each { val ->
                        if(val instanceof Map) {
                            Map result = (Map) val
                            // component and loadInfo are required for a summary result, if it has one of them assume it is a summary result
                            boolean isSummary = result.containsKey("component") || result.containsKey("loadInfo")
                            // components and analysisCase are required for a detailed result, if it has one of them assume it is a detailed result
                            boolean isDetailed = result.containsKey("components") || result.containsKey("analysisCase")

                            if(isDetailed) {
                                removeAdditionalProperties(result, detailedSchema)
                            } else if(isSummary) {
                                removeAdditionalProperties(result, summarySchema)
                            } else {
                                log.warn("Unable to determine analysis type, summary or detailed, not removing additional properties from the result object: ${val}")
                            }
                        }
                    }
                } else {
                    boolean itemOneOfAllEnum = items.get("oneOf")?.every { it.get("enum") != null } // tags "oneOf": [ enum, enum ]

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
                    boolean valIsMap = (val instanceof Map)
                    boolean itemIsObject = (items.has(index) && items.get(index).get("type")?.asText() == "object")

                    if (valIsMap && itemIsObject) {
                        removeAdditionalProperties((Map) val, items.get(index))
                    }
                }
            }
        }
    }
}
