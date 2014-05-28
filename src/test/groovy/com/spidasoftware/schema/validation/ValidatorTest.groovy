package com.spidasoftware.schema.validation

import junit.framework.TestCase

/**
 * Created with IntelliJ IDEA.
 * User: mford
 * Date: 7/24/13
 * Time: 2:45 PM
 */
class ValidatorTest extends TestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	void testOneOfEverythingObject() throws Exception {
		def schema = "/v1/schema/spidacalc/calc/structure.schema"
		def instance = new File("resources/v1/examples/spidacalc/designs/one_of_everything.json").text
		def report = new Validator().validateAndReport(schema, instance)
		assertTrue "the instance should be valid against a schema", report.isSuccess()

		def exception = null
		try {
			new Validator().validate(schema, instance)
		} catch (com.spidasoftware.schema.server.JSONServletException jse) {
			exception = jse
		}
		assertNull("Should not have thrown a servlet exception", exception)
	}

	void testBadObject() throws Exception {
		def schema = "/v1/schema/spidacalc/calc/structure.schema"
		def instance = '{"id":"externalId", "distance":{"unit":"FOOT", "value":10}, "direction":0}'
		def report = new Validator().validateAndReport(schema, instance)
		assertFalse "the instance should be valid against a schema", report.isSuccess()

		def exception = null
		try {
			new Validator().validate(schema, instance)
		} catch (com.spidasoftware.schema.server.JSONServletException jse) {
			exception = jse
		}
	}

	void testBadInput() throws Exception {
		def badSchema = "/"
		def schema = "/v1/schema/spidacalc/calc/structure.schema"
		def instance = '{"id":"externalId", "distance":{"unit":"FOOT", "value":10}, "direction":0}'
		def exception = null
		try {
			new Validator().validate(schema, instance)
		} catch (com.spidasoftware.schema.server.JSONServletException jse) {
			exception = jse
		}
		assertNotNull("Should have thrown a servlet exception", exception)

		def badInstance = "{{";
		exception = null
		try {
			new Validator().validate(schema, instance)
		} catch (com.spidasoftware.schema.server.JSONServletException jse) {
			exception = jse
		}
		assertNotNull("Should have thrown a servlet exception", exception)
	}

	void testFromText() throws Exception {
		def schemaText=  '''
{
  "description": "This is for testing schemas as text instead of as paths to a file.",
  "type": "object",
  "required": [
    "properties"
  ],
  "properties": {
    "externalId": {
      "description": "Option unique id for tracking within integrator systems.",
      "type": "string"
    }
  }
}
'''
		def instance = '{"properties":{"externalId":"abc123"}}'
		def report = new Validator().validateAndReportFromText(schemaText, instance)
		report.each{println it}
		assertTrue "the instance should be valid against a schema", report.isSuccess()

		def exception = null
		try {
			new Validator().validateFromText(schemaText, instance)
		} catch (com.spidasoftware.schema.server.JSONServletException jse) {
			exception = jse
		}
		assertNull("Should not have thrown a servlet exception", exception)
	}

	void testFromFile() {
		def schemaFile = new File("resources/v1/schema/spidacalc/calc/structure.schema")
		def instance = new File("resources/v1/examples/spidacalc/designs/one_of_everything.json").text
		def report = new Validator().validateAndReport(schemaFile, instance)
		assertTrue "the instance should be valid against a schema", report.isSuccess()

		def exception = null
		try {
			new Validator().validate(schemaFile, instance)
		} catch (com.spidasoftware.schema.server.JSONServletException jse) {
			exception = jse
		}
		assertNull("Should not have thrown a servlet exception", exception)
	}

	void testFromURL() {
		def schemaURL = this.getClass().getResource("/v1/schema/spidacalc/calc/structure.schema")
		def instance = new File("resources/v1/examples/spidacalc/designs/one_of_everything.json").text
		def report = new Validator().validateAndReport(schemaURL, instance)
		assertTrue "the instance should be valid against a schema", report.isSuccess()

		def exception = null
		try {
			new Validator().validate(schemaURL, instance)
		} catch (com.spidasoftware.schema.server.JSONServletException jse) {
			exception = jse
		}
		assertNull("Should not have thrown a servlet exception", exception)
	}
}
