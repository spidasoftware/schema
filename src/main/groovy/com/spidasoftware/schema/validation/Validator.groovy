/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.SchemaVersion
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.cfg.ValidationConfigurationBuilder
import com.github.fge.jsonschema.exceptions.ProcessingException
import com.github.fge.jsonschema.library.DraftV4Library
import com.github.fge.jsonschema.library.Library
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.load.configuration.LoadingConfigurationBuilder
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.ref.JsonRef
import com.github.fge.jsonschema.report.LogLevel
import com.github.fge.jsonschema.report.ProcessingReport
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import org.apache.commons.io.FilenameUtils

/**
 * Class to validate against our json schemas. Will references the schemas locally as a jar resource.
 */
@Log4j
@CompileStatic
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

	protected ProcessingReport loadAndValidate(String schemaPath, JsonNode jsonNode) {
		String namespace = convertToResourcePath(schemaPath)
		JsonNode schemaNode = JsonLoader.fromResource(schemaPath)
		return validateWithStrictModeCheck(jsonNode, schemaNode, namespace)
	}

	protected ProcessingReport validateUsingSchemaText(String schemaText, JsonNode jsonNode) {
		JsonNode schemaNode = JsonLoader.fromString(schemaText)
		return validateWithStrictModeCheck(jsonNode, schemaNode)
	}

	@CompileDynamic
	private ProcessingReport validateWithStrictModeCheck(JsonNode jsonNode, JsonNode schemaNode, String namespace = null){
		boolean ignoreAdditionalProperties = true
		JsonNode strictNode = jsonNode.get('strict')

		//remove strict it so it doesn't effect validation
		if(strictNode != null){
			ignoreAdditionalProperties = !strictNode.booleanValue()
			jsonNode.remove('strict')
		}

		def factory = createJsonSchemaFactory(namespace, ignoreAdditionalProperties)
		def report = factory.getJsonSchema(schemaNode).validate(jsonNode)

		//now revert the strict property change
		if(strictNode != null){
			jsonNode.put('strict', strictNode.booleanValue())
		}

		report.messages.retainAll{it.logLevel == LogLevel.ERROR}
		return report
	}

	@CompileDynamic
	private createJsonSchemaFactory(String namespace = null, boolean ignoreAdditionalProperties = true){
		ValidationConfigurationBuilder valCfgBuilder = ValidationConfiguration.newBuilder()
		if(ignoreAdditionalProperties){
			//this removes the AdditionalPropertiesValidator (See CommonValidatorDictionary)
			Library modifiedLib = DraftV4Library.get().thaw().removeKeyword("additionalProperties").freeze()
			valCfgBuilder.libraries.remove(JsonRef.fromString(SchemaVersion.DRAFTV4.location.toString()))
			valCfgBuilder.setDefaultLibrary(SchemaVersion.DRAFTV4.location.toString(), modifiedLib)
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

	String convertToResourcePath(String schemaPath){
		//FilenameUtils.getPath(filepath) gets the parent folder and removes path prefix (windows drive letter or unix tilde)
		//More Info: http://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/FilenameUtils.html#getPathNoEndSeparator(java.lang.String)
		String namespace = "/" + FilenameUtils.getPathNoEndSeparator(schemaPath)
		namespace = FilenameUtils.separatorsToUnix(namespace)

		String namespaceString = "resource:${namespace}/"
		log.trace "Validation: \nschemaPath=$schemaPath \nnamespace=$namespace \nnamespaceString=$namespaceString"
		return namespaceString
	}
}
