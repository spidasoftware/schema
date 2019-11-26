package com.spidasoftware.schema.validation

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator
//import com.github.fge.jsonschema.uri.*
import org.apache.log4j.Logger

class GeneralSchemaValidationTest extends GroovyTestCase {


	def log = Logger.getLogger(this.class)
	def report
	private static final SyntaxValidator schemaValidator = new SyntaxValidator(ValidationConfiguration.byDefault())
	final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(new File("resources").toURI().toString()).freeze()
  final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze()

	void setUp() {
	}

	void testThatAddressIsValidSchema(){
		def schemaNode = JsonLoader.fromPath("resources/schema/general/geometry.schema")
		report = schemaValidator.validateSchema(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testThatBearingIsValidSchema(){
		def schemaNode = JsonLoader.fromPath("resources/schema/general/bearing.schema")
		report = schemaValidator.validateSchema(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}
	void testThatGeometryIsValidSchema(){

		def instance =  '{"type": "POINT", "coordinates": []}'
		def schemaNode = JsonLoader.fromPath("resources/schema/general/geometry.schema")
		report = schemaValidator.validateSchema(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

		def schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode)
		report = schema.validate(JsonLoader.fromString(instance))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testThatIdIsValidSchema(){
		def schemaNode = JsonLoader.fromPath("resources/schema/general/id.schema")
		report = schemaValidator.validateSchema(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testThatMeasurableIsValidSchema(){
		def instance =  '{"unit": "FOOT", "value": 10}'
		def schemaNode = JsonLoader.fromPath("resources/schema/general/measurable.schema")
		report = schemaValidator.validateSchema(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

		def schema = factory.getJsonSchema("schema/general/measurable.schema")

		report = schema.validate(JsonLoader.fromString(instance))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testThatMethodResponseIsValidSchema(){
		def schemaNode = JsonLoader.fromPath("resources/schema/general/method_response.schema")
		report = schemaValidator.validateSchema(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testThatOwnerIsValidSchema(){
		def schemaNode = JsonLoader.fromPath("resources/schema/general/owner.schema")
		report = schemaValidator.validateSchema(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testOwnerMinLength() {
		def json = JsonLoader.fromString('{"name":"", "industry":"COMMUNICATION"}')
		def schemaNode = JsonLoader.fromPath("resources/schema/general/owner.schema")

		def schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode)
		report = schema.validate(json)
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testThatServiceMethodIsValidSchema(){
		def schemaNode = JsonLoader.fromPath("resources/schema/general/service_method.schema")
		report = schemaValidator.validateSchema(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

}
