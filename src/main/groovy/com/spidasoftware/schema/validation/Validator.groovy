package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.exceptions.ProcessingException
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.JsonSchema
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.report.ProcessingReport
import org.apache.log4j.Logger
import com.spidasoftware.schema.server.*

/**
 * Class to validate against our json schemas. Will references the schemas locally as a jar resource.
 * User: mford
 * Date: 7/24/13
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
class Validator {
	Logger log = Logger.getLogger(Validator.class);

	/**
	 *
	 * @param schemaPath resource URL to the schema. eg, "/spidacalc/calc/project.schema"
	 * @param json string representation of json to be validated.
	 * @return The fge schema-validator report
	 */
	public ProcessingReport validateAndReport(String schemaPath, String json) {
		try {

			String namespace = new File(schemaPath).getParentFile().getCanonicalPath();
			String namespaceString = "resource:" + namespace + "/";
			log.debug("namespaceString = " + namespaceString)
			LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(namespaceString).freeze();
			JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
			JsonNode instance = JsonLoader.fromString(json);
			JsonNode schemaNode = JsonLoader.fromResource(schemaPath);

			JsonSchema schema = factory.getJsonSchema(schemaNode);
			ProcessingReport report = schema.validate(instance);
			return report
		} catch (IOException e) {
			log.error(e, e);
		} catch (ProcessingException e) {
			log.error(e, e);
		}
		return null;
	}

	/**
	 *
	 * @param schemaPath resource URL to the schema. eg, "/spidacalc/calc/project.schema"
	 * @param json string representation of json to be validated.
	 * @throws JSONServletException Throws an exception if validation failed. This exception will include a more detailed report
	 */
	public void validate(String schemaPath, String json) throws JSONServletException{
		ProcessingReport report = validateAndReport(schemaPath, json)

		if (report == null) {
			throw new JSONServletException(JSONServletException.INTERNAL_ERROR, "An internal error occurred when validating JSON")
		}
		if (!report.isSuccess()) {
			throw new JSONServletException(JSONServletException.INVALID_PARAMETERS, report.toString());
		}
	}


}
