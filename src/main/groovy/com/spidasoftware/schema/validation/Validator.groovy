package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.JsonNode

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.exceptions.ProcessingException
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.JsonSchema
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.report.ProcessingReport

import com.spidasoftware.schema.server.*

import org.apache.commons.io.FilenameUtils
import org.apache.log4j.Logger

/**
 * Class to validate against our json schemas. Will references the schemas locally as a jar resource.
 * The resources directory is specified in pom.xml.
 * 
 * User: mford
 * Date: 7/24/13
 * Time: 2:34 PM
 */
class Validator {
	Logger log = Logger.getLogger(Validator.class);

	/**
	 *
	 * @param schemaPath resource URL to the schema. eg, "/v1/schema/spidacalc/calc/project.schema"
	 * @param json string representation of json to be validated.
	 * @return The fge schema-validator report
	 */
	public ProcessingReport validateAndReport(String schemaPath, String json) {
		catchAndLogExceptions {
			//FilenameUtils.getPath(filepath) gets the parent folder and removes path prefix (windows drive letter or unix tilde)
			//More Info: http://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/FilenameUtils.html#getPathNoEndSeparator(java.lang.String)
			String namespace = "/" + FilenameUtils.getPathNoEndSeparator(schemaPath);
			namespace = FilenameUtils.separatorsToUnix(namespace);

			String namespaceString = "resource:" + namespace + "/";
			log.trace "Validation: \nschemaPath=$schemaPath \nnamespace=$namespace \nnamespaceString=$namespaceString"
			
			JsonSchemaFactory factory = getFactoryWithNamespace(namespaceString);
			JsonNode schemaNode = JsonLoader.fromResource(schemaPath);
			return loadAndValidate(factory, schemaNode, json);
		}
	}

		/**
	 *
	 * @param File object containing a schema.
	 * @param json string representation of json to be validated.
	 * @return The fge schema-validator report
	 */
	public ProcessingReport validateAndReport(File schemaFile, String json) {
		catchAndLogExceptions {
			String namespaceString = "file:/" + FilenameUtils.getPath(schemaFile.canonicalPath);
			JsonSchemaFactory factory = getFactoryWithNamespace(namespaceString);
			JsonNode schemaNode = JsonLoader.fromFile(schemaFile);
			return loadAndValidate(factory, schemaNode, json);
		}
	}

	/**
	 *
	 * @param URL to the schema. eg, "http://json-schema.org/draft-04/schema#"
	 * @param json string representation of json to be validated.
	 * @return The fge schema-validator report
	 */
	public ProcessingReport validateAndReport(URL schemaUrl, String json) {
		catchAndLogExceptions {
			String namespaceString = FilenameUtils.getPath(schemaUrl.toString());
			JsonSchemaFactory factory = getFactoryWithNamespace(namespaceString);
			JsonNode schemaNode = JsonLoader.fromURL(schemaUrl);
			return loadAndValidate(factory, schemaNode, json);
		}
	}

	/**
	 *
	 * @param A schema in plain text.
	 * @param json string representation of json to be validated.
	 * @return The fge schema-validator report
	 */
	public ProcessingReport validateAndReportFromText(String schemaText, String json) {
		catchAndLogExceptions {
			JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			JsonNode schemaNode = JsonLoader.fromString(schemaText);
			return loadAndValidate(factory, schemaNode, json);
		}
	}

	private def catchAndLogExceptions(closure) {
		try {
			return closure();
		} catch (IOException e) {
			log.error(e, e);
		} catch (ProcessingException e) {
			log.error(e, e);
		}
		return null;
	}

	private JsonSchemaFactory getFactoryWithNamespace(String namespaceString) {
		LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(namespaceString).freeze();
		JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
		return factory;
	}

	private ProcessingReport loadAndValidate(JsonSchemaFactory factory, JsonNode schemaNode, String json) {
		JsonSchema schema = factory.getJsonSchema(schemaNode);
		JsonNode instance = JsonLoader.fromString(json);
		ProcessingReport report = schema.validate(instance);
		return report;
	}

	/**
	 *
	 * @param schemaPath resource URL to the schema. eg, "/spidacalc/calc/project.schema"
	 * @param json string representation of json to be validated.
	 * @throws JSONServletException Throws an exception if validation failed. This exception will include a more detailed report
	 */
	public void validate(String schemaPath, String json) throws JSONServletException{
		ProcessingReport report = validateAndReport(schemaPath, json)
		handleReport(report)
	}

	/**
	 *
	 * @param File object containing a schema.
	 * @param json string representation of json to be validated.
	 * @return The fge schema-validator report
	 */
	public void validate(File schemaFile, String json) throws JSONServletException{
		ProcessingReport report = validateAndReport(schemaFile, json)
		handleReport(report)
	}

	/**
	 *
	 * @param URL to the schema. eg, "http://json-schema.org/draft-04/schema#"
	 * @param json string representation of json to be validated.
	 * @return The fge schema-validator report
	 */
	public void validate(URL schemaURL, String json) throws JSONServletException{
		ProcessingReport report = validateAndReport(schemaURL, json)
		handleReport(report)
	}

	/**
	 *
	 * @param A schema in plain text.
	 * @param json string representation of json to be validated.
	 * @return The fge schema-validator report
	 */
	public void validateFromText(String schema, String json) throws JSONServletException{
		ProcessingReport report = validateAndReportFromText(schema, json)
		handleReport(report)
	}

	private void handleReport(ProcessingReport report) throws JSONServletException {
		if (report == null) {
			throw new JSONServletException(JSONServletException.INTERNAL_ERROR, "An internal error occurred when validating JSON");
		}
		if (!report.isSuccess()) {
			throw new JSONServletException(JSONServletException.INVALID_PARAMETERS, report.toString());
		}
	}
}
