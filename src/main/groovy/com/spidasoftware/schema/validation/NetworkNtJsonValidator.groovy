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
 * Removed CompileStatic to allow ArrayNode to work with validateWithStrictModeCheck
 */
@Log4j
class NetworkNtJsonValidator extends Validator {

	private Map<String, JsonNode> schemaPathCache = [:]
	private Map<String, JsonNode> schemaTextCache = [:]
	private Map<JsonNode, JsonSchema> schemaCacheStrict = [:]
	private Map<JsonNode, JsonSchema> schemaCacheNotStrict = [:]
	private JsonSchemaFactory schemaFactoryStrict = null
	private JsonSchemaFactory schemaFactoryNotStrict = null

	@Override
	protected ProcessingReport loadAndValidate(String schemaPath, JsonNode jsonNode) {
		JsonNode schemaNode = schemaPathCache.get(schemaPath)
		if(schemaNode == null) {
			schemaNode = (new ObjectMapper()).readTree(this.class.getResource(schemaPath))
			schemaPathCache.put(schemaPath, schemaNode)
		}
		return validateWithStrictModeCheck(jsonNode, schemaNode, this.class.getResource(schemaPath).toURI())
	}

	@Override
	protected ProcessingReport validateUsingSchemaText(String schemaText, JsonNode jsonNode) {
		JsonNode schemaNode = schemaTextCache.get(schemaText)
		if(schemaNode == null) {
			schemaNode = (new ObjectMapper()).readTree(schemaText)
			schemaTextCache.put(schemaText, schemaNode)
		}
		return validateWithStrictModeCheck(jsonNode, schemaNode)
	}

	private ProcessingReport validateWithStrictModeCheck(JsonNode jsonNode, JsonNode schemaNode, URI schemaUri = null){
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
								new NonValidationKeyword("additionalProperties")  // this suppresses the additionalProperties validation
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
}
