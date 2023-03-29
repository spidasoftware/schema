/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.exceptions.ProcessingException
import com.github.fge.jsonschema.report.ListProcessingReport
import com.github.fge.jsonschema.report.ProcessingMessage
import com.github.fge.jsonschema.report.ProcessingReport
import com.networknt.schema.JsonMetaSchema
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.NonValidationKeyword
import com.networknt.schema.SpecVersion
import com.networknt.schema.ValidationMessage
import com.networknt.schema.ValidatorTypeCode
import groovy.transform.CompileDynamic
import groovy.util.logging.Log4j

/**
 * Class to validate against our json schemas. Will references the schemas locally as a jar resource.
 * Uses networkNT for work now, but still reports as ProcessingReport to maintain compatibility
 */
@Log4j
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

	@CompileDynamic
	private JsonSchemaFactory getJsonSchemaFactory(boolean ignoreAdditionalProperties) {
		if(ignoreAdditionalProperties) {
			if(schemaFactoryNotStrict == null) {
				JsonMetaSchema jsonMetaSchema = new JsonMetaSchema.Builder(JsonMetaSchema.V4.URI)
						.idKeyword(JsonMetaSchema.V4.ID)
						.addFormats(JsonMetaSchema.V4.BUILTIN_FORMATS)
						.addKeywords(ValidatorTypeCode.getNonFormatKeywords(SpecVersion.VersionFlag.V4))
				// keywords that may validly exist, but have no validation aspect to them
						.addKeywords(Arrays.asList(
								new NonValidationKeyword('$schema'),
								new NonValidationKeyword("id"),
								new NonValidationKeyword("title"),
								new NonValidationKeyword("description"),
								new NonValidationKeyword("default"),
								new NonValidationKeyword("definitions"),
								new NonValidationKeyword("additionalProperties")// this suppresses the additionalProperties validation
						))
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
			log.error(e, e)
		} catch (ProcessingException e) {
			log.error(e, e)
		}
		return null
	}
}
