package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.SchemaVersion
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.cfg.ValidationConfigurationBuilder
import com.github.fge.jsonschema.exceptions.ProcessingException
import com.github.fge.jsonschema.library.DraftV4Library
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.load.configuration.LoadingConfigurationBuilder
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.ref.JsonRef
import com.github.fge.jsonschema.report.ProcessingReport
import groovy.util.logging.Log4j
import org.apache.commons.io.FilenameUtils

/**
 * Class to validate against our json schemas. Will references the schemas locally as a jar resource.
 */
@Log4j
class Validator {

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

	private ProcessingReport loadAndValidate(String schemaPath, JsonNode jsonNode) {
		String namespace = convertToResourcePath(schemaPath)
		JsonNode schemaNode = JsonLoader.fromResource(schemaPath)
		return validateWithStrictModeCheck(jsonNode, schemaNode, namespace)
	}

	private ProcessingReport validateUsingSchemaText(String schemaText, JsonNode jsonNode) {
		JsonNode schemaNode = JsonLoader.fromString(schemaText)
		return validateWithStrictModeCheck(jsonNode, schemaNode)
	}

	private ProcessingReport validateWithStrictModeCheck(JsonNode jsonNode, JsonNode schemaNode, String namespace = null){
		boolean ignoreAdditionalProperties = true
		JsonNode strictNode = jsonNode.get('strict')
		if(strictNode != null){
			ignoreAdditionalProperties = !strictNode.booleanValue()
			jsonNode.remove('strict')
		}

		def factory = createJsonSchemaFactory(namespace, ignoreAdditionalProperties)

		def result = factory.getJsonSchema(schemaNode).validate(jsonNode)

		if(strictNode != null){
			jsonNode.put('strict', strictNode.booleanValue())
		}

		return result
	}

	private createJsonSchemaFactory(String namespace = null, boolean ignoreAdditionalProperties = true){
		ValidationConfigurationBuilder valCfgBuilder = ValidationConfiguration.newBuilder()
		if(ignoreAdditionalProperties){
			def lib = DraftV4Library.get().thaw().removeKeyword("additionalProperties").freeze()
			valCfgBuilder.libraries.remove(JsonRef.fromString(SchemaVersion.DRAFTV4.location.toString()))
			valCfgBuilder.setDefaultLibrary(SchemaVersion.DRAFTV4.location.toString(), lib)
		}

		LoadingConfigurationBuilder loadCfgBuilder = LoadingConfiguration.newBuilder()
		if(namespace){
			loadCfgBuilder.setNamespace(namespace)
		}

		return JsonSchemaFactory.newBuilder()
					.setLoadingConfiguration(loadCfgBuilder.freeze())
					.setValidationConfiguration(valCfgBuilder.freeze())
					.freeze()
	}

	private void handleReport(ProcessingReport report) throws JSONServletException {
		if (report == null) {
			throw new JSONServletException(JSONServletException.INTERNAL_ERROR, "An internal error occurred when validating JSON")
		}
		if (!report.isSuccess()) {
			throw new JSONServletException(JSONServletException.BAD_REQUEST, report.toString())
		}
	}

	private def catchAndLogExceptions(closure) {
		try {
			return closure()
		} catch (IOException e) {
			log.error(e, e)
		} catch (ProcessingException e) {
			log.error(e, e)
		}
		return null
	}

	String convertToResourcePath(schemaPath){
		//FilenameUtils.getPath(filepath) gets the parent folder and removes path prefix (windows drive letter or unix tilde)
		//More Info: http://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/FilenameUtils.html#getPathNoEndSeparator(java.lang.String)
		String namespace = "/" + FilenameUtils.getPathNoEndSeparator(schemaPath)
		namespace = FilenameUtils.separatorsToUnix(namespace)

		String namespaceString = "resource:${namespace}/"
		log.trace "Validation: \nschemaPath=$schemaPath \nnamespace=$namespace \nnamespaceString=$namespaceString"
		return namespaceString
	}

}
