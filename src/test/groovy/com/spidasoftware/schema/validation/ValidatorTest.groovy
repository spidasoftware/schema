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
		def schema = "/schema/spidacalc/calc/structure.schema"
		def instance = new File("resources/examples/spidacalc/designs/one_of_everything.json").text
		def report = new Validator().validateAndReport(schema, instance)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

		def exception = null
		try {
			new Validator().validate(schema, instance)
		} catch (JSONServletException jse) {
			exception = jse
		}
		assertNull("Should not have thrown a servlet exception", exception)
	}

	void testBadObject() throws Exception {
		def schema = "/schema/spidacalc/calc/structure.schema"
		def instance = '{"id":"externalId", "distance":{"unit":"FOOT", "value":10}, "direction":0}'
		def report = new Validator().validateAndReport(schema, instance)
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()

		def exception = null
		try {
			new Validator().validate(schema, instance)
		} catch (JSONServletException jse) {
			exception = jse
		}
	}

	void testBadInput() throws Exception {
		def badSchema = "/"
		def schema = "/schema/spidacalc/calc/structure.schema"
		def instance = '{"id":"externalId", "distance":{"unit":"FOOT", "value":10}, "direction":0}'
		def exception = null
		try {
			new Validator().validate(schema, instance)
		} catch (JSONServletException jse) {
			exception = jse
		}
		assertNotNull("Should have thrown a servlet exception", exception)

		def badInstance = "{{";
		exception = null
		try {
			new Validator().validate(schema, instance)
		} catch (JSONServletException jse) {
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
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

		def exception = null
		try {
			new Validator().validateFromText(schemaText, instance)
		} catch (JSONServletException jse) {
			exception = jse
		}
		assertNull("Should not have thrown a servlet exception", exception)
	}

	void testFromFile() {
		def schemaFile = new File("resources/schema/spidacalc/calc/structure.schema")
		def instance = new File("resources/examples/spidacalc/designs/one_of_everything.json").text
		def report = new Validator().validateAndReport(schemaFile, instance)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

		def exception = null
		try {
			new Validator().validate(schemaFile, instance)
		} catch (JSONServletException jse) {
			exception = jse
		}
		assertNull("Should not have thrown a servlet exception", exception)
	}

	void testFromURL() {
		def schemaURL = this.getClass().getResource("/schema/spidacalc/calc/structure.schema")
		def instance = new File("resources/examples/spidacalc/designs/one_of_everything.json").text
		def report = new Validator().validateAndReport(schemaURL, instance)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

		def exception = null
		try {
			new Validator().validate(schemaURL, instance)
		} catch (JSONServletException jse) {
			exception = jse
		}
		assertNull("Should not have thrown a servlet exception", exception)
	}
}
