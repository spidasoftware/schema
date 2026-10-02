/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.exceptions.ProcessingException
import com.github.fge.jsonschema.core.report.ListProcessingReport
import com.github.fge.jsonschema.core.report.ProcessingMessage
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.networknt.schema.JsonMetaSchema
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.NonValidationKeyword
import com.networknt.schema.SpecVersion
import com.networknt.schema.ValidationMessage
import groovy.util.logging.Slf4j

/**
 * Class to validate against our json schemas. Will references the schemas locally as a jar resource.
 * Uses networkNT for work now, but still reports as ProcessingReport to maintain compatibility
 */
@Slf4j
class Validator {

	private Map<String, JsonNode> schemaPathCache = [:]
	private Map<String, JsonNode> schemaTextCache = [:]
	private Map<JsonNode, JsonSchema> schemaCacheStrict = [:]
	private Map<JsonNode, JsonSchema> schemaCacheNotStrict = [:]
	private JsonSchemaFactory schemaFactoryStrict = null
	private JsonSchemaFactory schemaFactoryNotStrict = null

	protected ProcessingReport loadAndValidate(String schemaPath, JsonNode jsonNode) {
		JsonNode schemaNode = schemaPathCache.get(schemaPath)
		if(schemaNode == null) {
			schemaNode = (new ObjectMapper()).readTree(this.class.getResource(schemaPath))
			schemaPathCache.put(schemaPath, schemaNode)
		}
		return validateWithStrictModeCheck(jsonNode, schemaNode, this.class.getResource(schemaPath).toURI())
	}

	protected ProcessingReport validateUsingSchemaText(String schemaText, JsonNode jsonNode) {
		JsonNode schemaNode = schemaTextCache.get(schemaText)
		if(schemaNode == null) {
			schemaNode = (new ObjectMapper()).readTree(schemaText)
			schemaTextCache.put(schemaText, schemaNode)
		}
		return validateWithStrictModeCheck(jsonNode, schemaNode)
	}

	ProcessingReport validateWithStrictModeCheck(JsonNode jsonNode, JsonNode schemaNode, URI schemaUri = null){
		boolean ignoreAdditionalProperties = true
		JsonNode strictNode = jsonNode.get('strict')

		//remove strict it so it doesn't effect validation
		if(strictNode != null){
			ignoreAdditionalProperties = !strictNode.booleanValue()
			jsonNode.remove('strict')
		}

		Map<JsonNode, JsonSchema> schemaCache
		if(ignoreAdditionalProperties) {
			schemaCache = schemaCacheNotStrict
		} else {
			schemaCache = schemaCacheStrict
		}
		JsonSchema schema = schemaCache.get(schemaNode)
		if(schema == null) {
			JsonSchemaFactory factory = getJsonSchemaFactory(ignoreAdditionalProperties)  // todo cache these too
			if (schemaUri == null) {  // try (schemaUri, schemaNode) regardless of nullness
				schema = factory.getSchema(schemaNode)
			} else {
				schema = factory.getSchema(schemaUri, schemaNode)
			}
			schemaCache.put(schemaNode, schema)
		}

		Set<ValidationMessage> validationMessages = schema.validate(jsonNode)

		//now revert the strict property change
		if(strictNode != null){
			jsonNode.put('strict', strictNode.booleanValue())
		}

		ListProcessingReport processingReport = new ListProcessingReport()
		for(ValidationMessage validationMessage in validationMessages){
			ProcessingMessage processingMessage = new ProcessingMessage()
			processingMessage.setMessage(validationMessage.message)
			processingReport.error(processingMessage)
		}
		return processingReport
	}

	private JsonSchemaFactory getJsonSchemaFactory(boolean ignoreAdditionalProperties) {
		if(ignoreAdditionalProperties) {
			if(schemaFactoryNotStrict == null) {
				JsonMetaSchema v4 = JsonMetaSchema.getV4()
				JsonMetaSchema jsonMetaSchema = JsonMetaSchema.builder(v4.getUri(), v4)
						// overrides the V4 validator so additionalProperties is parsed but not enforced
						.addKeyword(new NonValidationKeyword("additionalProperties"))
						.build()

				schemaFactoryNotStrict = JsonSchemaFactory.builder()
						.defaultMetaSchemaURI(jsonMetaSchema.getUri())
						.addMetaSchema(jsonMetaSchema)
						.build()
			}
			return schemaFactoryNotStrict
		} else {
			if(schemaFactoryStrict == null) {
				schemaFactoryStrict = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4)
				return schemaFactoryStrict
			}
			return schemaFactoryStrict
		}
	}

	/**
	 * @param schemaPath resource URL to the schema. eg, "/v1/schema/spidacalc/calc/project.schema"
	 * @param json string representation of json to be validated.
	 * @return The fge schema-validator report
	 */
	ProcessingReport validateAndReport(String schemaPath, String json) {
		catchAndLogExceptions {
			JsonNode jsonNode = JsonLoader.fromString(json)
			return loadAndValidate(schemaPath, jsonNode)
		}
	}

	/**
	 * @param schemaPath resource URL to the schema. eg, "/v1/schema/spidacalc/calc/project.schema"
	 * @param json JSONObject to be validated.
	 * @return The fge schema-validator report
	 */
	ProcessingReport validateAndReport(String schemaPath, Map json) {
		catchAndLogExceptions {
			JsonNode jsonNode = new ObjectMapper().valueToTree(json)
			return loadAndValidate(schemaPath, jsonNode)
		}
	}

	/**
	 * @param schemaPath resource URL to the schema. eg, "/v1/schema/spidacalc/calc/project.schema"
	 * @param jsonNode JsonNode to be validated.
	 * @return The fge schema-validator report
	 */
	ProcessingReport validateAndReport(String schemaPath, JsonNode jsonNode) {
		catchAndLogExceptions {
			return loadAndValidate(schemaPath, jsonNode)
		}
	}

	/**
	 * @param schemaText A schema in plain text.
	 * @param son string representation of json to be validated.
	 * @return The fge schema-validator report
	 */
	ProcessingReport validateAndReportFromText(String schemaText, String json) {
		catchAndLogExceptions {
			JsonNode jsonNode = JsonLoader.fromString(json)
			return validateUsingSchemaText(schemaText, jsonNode)
		}
	}

	/**
	 * @param schemaPath resource URL to the schema. eg, "/spidacalc/calc/project.schema"
	 * @param json string representation of json to be validated.
	 * @throws JSONServletException Throws an exception if validation failed. This exception will include a more detailed report
	 */
	void validate(String schemaPath, String json) throws JSONServletException {
		handleReport(validateAndReport(schemaPath, json))
	}

	/**
	 * @param schemaPath resource URL to the schema. eg, "/spidacalc/calc/project.schema"
	 * @param json JSONObject to be validated.
	 * @throws JSONServletException Throws an exception if validation failed. This exception will include a more detailed report
	 */
	void validate(String schemaPath, Map json) throws JSONServletException {
		handleReport(validateAndReport(schemaPath, json))
	}

	/**
	 * @param schemaText A schema in plain text.
	 * @param son string representation of json to be validated.
	 * @throws JSONServletException Throws an exception if validation failed. This exception will include a more detailed report
	 */
	void validateFromText(String schemaText, String json) {
		handleReport(validateAndReportFromText(schemaText, json))
	}

	protected void handleReport(ProcessingReport report) throws JSONServletException {
		if (report == null) {
			throw new JSONServletException(JSONServletException.INTERNAL_ERROR, "An internal error occurred when validating JSON")
		}
		if (!report.isSuccess()) {
			throw new JSONServletException(JSONServletException.BAD_REQUEST, report.toString())
		}
	}

	protected <T> T catchAndLogExceptions(Closure<T> closure) {
		try {
			return closure()
		} catch (IOException e) {
			log.error(e.toString(), e)
		} catch (ProcessingException e) {
			log.error(e.toString(), e)
		}
		return null
	}
}
