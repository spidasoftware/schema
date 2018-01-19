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
        removeAdditionalProperties(projectJSON,  JsonLoader.fromResource("/schema/spidacalc/calc/project-v4.schema"))
    }

    @Override
    void applyToLocation(Map locationJSON) throws ConversionException {
        // Do nothing
    }

    @Override
    void revertLocation(Map locationJSON) throws ConversionException {
        removeAdditionalProperties(locationJSON,  JsonLoader.fromResource("/schema/spidacalc/calc/location-v4.schema"))
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        // Do nothing
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        removeAdditionalProperties(designJSON,  JsonLoader.fromResource("/schema/spidacalc/calc/design-v4.schema"))
    }

    // These fields can have any value they want, these won't be checked
    private static final List<String> SCHEMA_ALLOWS_ANYTHING = ["userDefinedValues"]
    // structure.sidewalkBraces doesn't have a type in the 7.0 schema, it is an array, it also doesn't have an id, use the description to identify.
    private static final List<String> DESCRIPTION_ARRAY_TYPE_OVERRIDES = ["List of all sidewalk braces (AKA queen posts)"]

    private void removeAdditionalProperties(Map json, JsonNode schemaNode) {
        boolean schemaAllowsAnything = SCHEMA_ALLOWS_ANYTHING.contains(schemaNode.get("id")?.asText())
        JsonNode additionalProperties = schemaNode.get("additionalProperties")
        boolean additionalPropertiesAllowed = additionalProperties == null || additionalProperties?.asBoolean()

        if (!schemaAllowsAnything && !additionalPropertiesAllowed) {
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
                boolean isResult = items.get("oneOf")?.any { it.get("id")?.asText() == "#/spidacalc/results/result.schema" }
                boolean isAssembly = items.get("anyOf")?.any { it.get("id")?.asText() == "#/spidacalc/calc/input_assembly.schema" }
                boolean itemOneOfAllEnum = items.get("oneOf")?.every { it.get("enum") != null } // tags "oneOf": [ enum, enum ]

                if(isResult) {
                    removeResultsAdditionalProperties(json, items)
                } else if(isAssembly) {
                    removeAssembliesAdditionalProperties(json, items)
                } else if(!itemOneOfAllEnum) {
                    json.each { val ->
                        if (val instanceof Map) {
                            removeAdditionalProperties((Map) val, items)
                        }
                    }
                }
            } else if(items.isArray()) {
                json.eachWithIndex { val, index ->
                    boolean valIsMap = (val instanceof Map)
                    boolean schemaExistsAndIsObject = (items.has(index) && items.get(index).get("type")?.asText() == "object")

                    if (valIsMap && schemaExistsAndIsObject) {
                        removeAdditionalProperties((Map) val, items.get(index))
                    }
                }
            }
        }


    }

    private void removeResultsAdditionalProperties(List json, JsonNode items) {
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
    }

    private void removeAssembliesAdditionalProperties(List json, JsonNode items) {
        JsonNode inputAssemblychema = items.get("anyOf").find { it.get("id")?.asText() == "#/spidacalc/calc/input_assembly.schema" }
        JsonNode assemblySchema = items.get("anyOf").find { it.get("id").asText() == "#/spidacalc/calc/assembly.schema" }

        json.each { val ->
            if(val instanceof Map) {
                Map assembly = (Map) val
                // source and id are required for assembly.schema, assume that it is an assembly, not an input assembly if it has either of these fields
                boolean notInputAssembly = assembly.containsKey("source") || assembly.containsKey("id")

                if(notInputAssembly) {
                    removeAdditionalProperties(assembly, assemblySchema)
                } else { // input_assembly
                    removeAdditionalProperties(assembly, inputAssemblychema)
                }
            }
        }
    }
}
