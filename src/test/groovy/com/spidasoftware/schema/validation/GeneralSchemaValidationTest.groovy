package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jackson.JsonLoader
import com.networknt.schema.JsonMetaSchema
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import org.apache.log4j.Logger

class GeneralSchemaValidationTest extends GroovyTestCase {
	def report
  	final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4)
	JsonSchema metaSchema = factory.getSchema(JsonMetaSchema.v4.getUri().toURI())
	Validator validator = new Validator()

	static JsonNode fromPath(String path) {
		def stream = Validator.getResourceAsStream(path)
		return new ObjectMapper().readValue(stream, JsonNode)
	}

	void testThatAddressIsValidSchema(){
		def schemaNode = fromPath("/schema/general/geometry.schema")
		def report = metaSchema.validate(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toListString()}", report.isEmpty()
	}

	void testThatBearingIsValidSchema(){
		def schemaNode = fromPath("/schema/general/bearing.schema")
		report = metaSchema.validate(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toListString()}", report.isEmpty()
	}
	void testThatGeometryIsValidSchema(){

		def schemaNode = fromPath("/schema/general/geometry.schema")
		report = metaSchema.validate(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toListString()}", report.isEmpty()

		report = validator.validateAndReport("/schema/general/geometry.schema", '{"type": "POINT", "coordinates": []}')
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testThatIdIsValidSchema(){
		def schemaNode = fromPath("/schema/general/id.schema")
		report = metaSchema.validate(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toListString()}", report.isEmpty()
	}

	void testThatMeasurableIsValidSchema(){
		def instance =  '{"unit": "FOOT", "value": 10}'
		def schemaNode = fromPath("/schema/general/measurable.schema")
		report = metaSchema.validate(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toListString()}", report.isEmpty()


		report = validator.validateAndReport("/schema/general/measurable.schema", instance)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testThatMethodResponseIsValidSchema(){
		def schemaNode = fromPath("/schema/general/method_response.schema")
		report = metaSchema.validate(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toListString()}", report.isEmpty()
	}

	void testThatOwnerIsValidSchema(){
		def schemaNode = fromPath("/schema/general/owner.schema")
		report = metaSchema.validate(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toListString()}", report.isEmpty()
	}

	void testOwnerMinLength() {
		def json = '{"name":"", "industry":"COMMUNICATION"}'
		def schemaNode = fromPath("/schema/general/owner.schema")
		report = metaSchema.validate(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toListString()}", report.isEmpty()

		report = validator.validateAndReport("/schema/general/owner.schema", json)
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testThatServiceMethodIsValidSchema(){
		def schemaNode = fromPath("/schema/general/service_method.schema")
		report = metaSchema.validate(schemaNode)
		assertTrue "this instance should be valid against the schema \n${report.toListString()}", report.isEmpty()
	}

}
