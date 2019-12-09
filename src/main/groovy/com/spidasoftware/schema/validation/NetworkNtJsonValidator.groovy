/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
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
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

/**
 * A json validator.  Hopefully faster than its superclass.
 */
@CompileStatic
@Log4j
class NetworkNtJsonValidator extends Validator {

	private Map<String, JsonNode> schemaPathCache = [:]
	private Map<String, JsonNode> schemaTextCache = [:]
	private Map<JsonNode, JsonSchema> schemaCache = [:]

	@Override
	protected ProcessingReport loadAndValidate(String schemaPath, JsonNode jsonNode) {
		JsonNode schemaNode = schemaPathCache.get(schemaPath)
		if(schemaNode == null) {
			schemaNode = (new ObjectMapper()).readTree(this.class.getResource(schemaPath))
			schemaPathCache.put(schemaPath, schemaNode)
		}
		return validateWithStrictModeCheck((ObjectNode)jsonNode, schemaNode, this.class.getResource(schemaPath).toURI())
	}

	@Override
	protected ProcessingReport validateUsingSchemaText(String schemaText, JsonNode jsonNode) {
		JsonNode schemaNode = schemaTextCache.get(schemaText)
		if(schemaNode == null) {
			schemaNode = (new ObjectMapper()).readTree(schemaText)
			schemaTextCache.put(schemaText, schemaNode)
		}
		return validateWithStrictModeCheck((ObjectNode)jsonNode, schemaNode)
	}

	private ProcessingReport validateWithStrictModeCheck(ObjectNode jsonNode, JsonNode schemaNode, URI schemaUri = null){
		boolean ignoreAdditionalProperties = true
		JsonNode strictNode = jsonNode.get('strict')

		//remove strict it so it doesn't effect validation
		if(strictNode != null){
			ignoreAdditionalProperties = !strictNode.booleanValue()
			jsonNode.remove('strict')
		}

		JsonSchema schema = schemaCache.get(schemaNode)
		if(schema == null) {
			JsonSchemaFactory factory = createJsonSchemaFactory(ignoreAdditionalProperties)
			if (schemaUri == null) {
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
		validationMessages.each { ValidationMessage validationMessage ->
			ProcessingMessage processingMessage = new ProcessingMessage()
			processingMessage.setMessage(validationMessage.message)
			processingReport.error(processingMessage)
		}
		return processingReport
	}

	@CompileDynamic
	private JsonSchemaFactory createJsonSchemaFactory(boolean ignoreAdditionalProperties) {
		JsonMetaSchema jsonMetaSchema
		if(ignoreAdditionalProperties) {
			jsonMetaSchema = new JsonMetaSchema.Builder(JsonMetaSchema.V4.URI)
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
							new NonValidationKeyword("additionalProperties")  // this suppresses the additionalProperties validation
					))
					.build()
		} else {
			jsonMetaSchema = JsonMetaSchema.getV4()
		}
		return JsonSchemaFactory.builder()
				.defaultMetaSchemaURI(jsonMetaSchema.getUri())
				.addMetaSchema(jsonMetaSchema)
				.build()
	}
}
