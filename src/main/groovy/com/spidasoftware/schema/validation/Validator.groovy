package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.exceptions.ProcessingException
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.JsonSchema
import com.github.fge.jsonschema.main.JsonSchemaFactory
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
	ProcessingReport validateAndReport(String schemaPath, String json, boolean ignoreAdditionalProperties = false) {
		catchAndLogExceptions {
			JsonNode jsonNode = JsonLoader.fromString(json)
			return loadAndValidate(schemaPath, jsonNode, ignoreAdditionalProperties)
		}
	}

	/**
	 * @param schemaPath resource URL to the schema. eg, "/v1/schema/spidacalc/calc/project.schema"
	 * @param json JSONObject to be validated.
	 * @return The fge schema-validator report
	 */
	ProcessingReport validateAndReport(String schemaPath, Map json, boolean ignoreAdditionalProperties = false) {
		catchAndLogExceptions {
			JsonNode jsonNode = new ObjectMapper().valueToTree(json)
			return loadAndValidate(schemaPath, jsonNode, ignoreAdditionalProperties)
		}
	}

	/**
	 * @param schemaText A schema in plain text.
	 * @param son string representation of json to be validated.
	 * @return The fge schema-validator report
	 */
	ProcessingReport validateAndReportFromText(String schemaText, String json, boolean ignoreAdditionalProperties = false) {
		catchAndLogExceptions {
			JsonNode jsonNode = JsonLoader.fromString(json)
			return validateUsingSchemaText(schemaText, jsonNode, ignoreAdditionalProperties)
		}
	}

	/**
	 * @param schemaPath resource URL to the schema. eg, "/spidacalc/calc/project.schema"
	 * @param json string representation of json to be validated.
	 * @throws JSONServletException Throws an exception if validation failed. This exception will include a more detailed report
	 */
	void validate(String schemaPath, String json, boolean ignoreAdditionalProperties = false) throws JSONServletException {
		handleReport(validateAndReport(schemaPath, json, ignoreAdditionalProperties))
	}

	/**
	 * @param schemaPath resource URL to the schema. eg, "/spidacalc/calc/project.schema"
	 * @param json JSONObject to be validated.
	 * @throws JSONServletException Throws an exception if validation failed. This exception will include a more detailed report
	 */
	void validate(String schemaPath, Map json, boolean ignoreAdditionalProperties = false) throws JSONServletException {
		handleReport(validateAndReport(schemaPath, json, ignoreAdditionalProperties))
	}

	/**
	 * @param schemaText A schema in plain text.
	 * @param son string representation of json to be validated.
	 * @throws JSONServletException Throws an exception if validation failed. This exception will include a more detailed report
	 */
	void validateFromText(String schemaText, String json) {
		handleReport(validateAndReportFromText(schemaText, json))
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

	private JsonSchemaFactory getFactoryWithNamespace(String namespaceString) {
		LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(namespaceString).freeze()
		JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze()
		return factory
	}

	private ProcessingReport loadAndValidate(String schemaPath, JsonNode jsonNode, boolean ignoreAdditionalProperties = false) {
		//FilenameUtils.getPath(filepath) gets the parent folder and removes path prefix (windows drive letter or unix tilde)
		//More Info: http://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/FilenameUtils.html#getPathNoEndSeparator(java.lang.String)
		String namespace = "/" + FilenameUtils.getPathNoEndSeparator(schemaPath)
		namespace = FilenameUtils.separatorsToUnix(namespace)

		String namespaceString = "resource:" + namespace + "/"
		log.trace "Validation: \nschemaPath=$schemaPath \nnamespace=$namespace \nnamespaceString=$namespaceString"
		JsonNode schemaNode = JsonLoader.fromResource(schemaPath)
		if (ignoreAdditionalProperties) {
			disableAdditionalPropertiesCheck(schemaNode)
		}

		JsonSchemaFactory factory = getFactoryWithNamespace(namespaceString)

		JsonSchema schema = factory.getJsonSchema(schemaNode)

		return schema.validate(jsonNode)
	}

	protected void disableAdditionalPropertiesCheck(JsonNode node) {
		if (node != null) {
			if (node.isObject()) {
				ObjectNode objectNode = node as ObjectNode
				objectNode.set("additionalProperties", BooleanNode.valueOf(true))
			}
			for (int i = 0; i < node.size(); i++) {
				disableAdditionalPropertiesCheck(node.get(i))
			}
		}
	}

	private ProcessingReport validateUsingSchemaText(String schemaText, JsonNode jsonNode) {
		JsonSchemaFactory factory = JsonSchemaFactory.byDefault()
		JsonNode schemaNode = JsonLoader.fromString(schemaText)
		JsonSchema schema = factory.getJsonSchema(schemaNode)
		return schema.validate(jsonNode)
	}

	private void handleReport(ProcessingReport report) throws JSONServletException {
		if (report == null) {
			throw new JSONServletException(JSONServletException.INTERNAL_ERROR, "An internal error occurred when validating JSON")
		}
		if (!report.isSuccess()) {
			throw new JSONServletException(JSONServletException.BAD_REQUEST, report.toString())
		}
	}
}
