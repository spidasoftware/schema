package com.spidasoftware.schema.validation

import junit.framework.TestCase
import org.apache.log4j.BasicConfigurator

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
		BasicConfigurator.configure();
	}

	void testOneOfEverythingObject() throws Exception {

		def schema = "/spidacalc/calc/structure.schema"
		def instance = new File("v2/examples/spidacalc/designs/one_of_everything.json").text
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
		def schema = "/spidacalc/calc/structure.schema"
		def instance = '{"id":"uuid", "distance":{"unit":"FOOT", "value":10}, "direction":0}'
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
		def schema = "/spidacalc/calc/structure.schema"
		def instance = '{"id":"uuid", "distance":{"unit":"FOOT", "value":10}, "direction":0}'
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

}
