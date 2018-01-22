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
        if(schemaNode != null) {
            boolean schemaAllowsAnything = SCHEMA_ALLOWS_ANYTHING.contains(schemaNode.get("id")?.asText())

            if (!schemaAllowsAnything) { // userDefinedValues can be anything
                List keysToRemove = []
                json.each { key, value ->
                    JsonNode schema = schemaNode.get("properties")?.get(key)

                    // If there isn't a property for this key, it should be removed
                    if (schema == null) {
                        keysToRemove.add(key)
                    } else {
                        boolean isObject = schema.get("type")?.asText() == "object"
                        boolean valueIsMap = (value instanceof Map)
                        boolean isArray = (schema.get("type")?.asText() == "array" || DESCRIPTION_ARRAY_TYPE_OVERRIDES.contains(schema.get("description")?.asText()))
                        boolean valueIsList = (value instanceof List)
                        // If this schema object has a properties map, we ignore the oneOf and anyOf, just check if the properties are allowed.
                        boolean hasProperties = schema.get("properties")?.isObject()
                        boolean schemaOneOfIsArray = !hasProperties && schema.get("oneOf")?.isArray()
                        boolean schemaAnyOfIsArray = !hasProperties && schema.get("anyOf")?.isArray()

                        if (valueIsMap && schemaOneOfIsArray) {
                            JsonNode matchedSchema = findSchemaForJson(json, schema.get("oneOf"))
                            removeAdditionalProperties((Map) value, matchedSchema)
                        } else if (valueIsMap && schemaAnyOfIsArray) {
                            JsonNode matchedSchema = findSchemaForJson(json, schema.get("anyOf"))
                            removeAdditionalProperties((Map) value, matchedSchema)
                        } else if (isObject && valueIsMap) {
                            removeAdditionalProperties((Map) value, schema)
                        } else if (isArray && valueIsList) {
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
    }

    private void removeAdditionalProperties(List json, JsonNode schema) {
        JsonNode items = schema.get("items")
        if (items != null) { // "components": { "type": "array" }
            if (items.isObject()) {
                // If this items object has a properties map, we ignore the oneOf and anyOf, just check if the properties are allowed.
                boolean hasProperties = items.get("properties")?.isObject()
                boolean oneOfIsArray = !hasProperties && items.get("oneOf")?.isArray() // results
                boolean anyOfIsArray = !hasProperties && items.get("anyOf")?.isArray() // assemblies

                json.each { val ->
                    if (val instanceof Map) {
                        JsonNode matchedSchema
                        if(oneOfIsArray) {
                            matchedSchema = findSchemaForJson(val, items.get("oneOf"))
                        } else if(anyOfIsArray) {
                            matchedSchema = findSchemaForJson(val, items.get("anyOf"))
                        } else {
                            matchedSchema = items
                        }

                        removeAdditionalProperties((Map) val, matchedSchema)
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

    /**
     * If the schema is oneOf, try to match the required properties to the object
     */
    private JsonNode findSchemaForJson(Map json, JsonNode possibleSchemas) {
        if(possibleSchemas?.isArray()) {
            JsonNode matchedSchema = possibleSchemas.find { it.get("required")?.isArray() }
            int numPropsMatch = getNumPropertiesMatchRequired(json, matchedSchema)
            possibleSchemas.each { JsonNode possibleSchema ->
                int tempNumPropsMatch = getNumPropertiesMatchRequired(json, possibleSchema)
                if (tempNumPropsMatch > numPropsMatch) {
                    numPropsMatch = tempNumPropsMatch
                    matchedSchema = possibleSchema
                }
            }
            return matchedSchema
        }
        return null
    }

    private int getNumPropertiesMatchRequired(Map json, JsonNode schema) {
        if(schema.get("required")?.isArray()) {
            return schema.get("required").count { required ->
                return json.containsKey(required.asText())
            }
        }
        return 0
    }
}
