package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Log4j
import spock.lang.Specification

@Log4j
class ValidatorTest extends Specification {

	void "test OneOfEverythingObject validate as a String"() {
		setup:
			def schema = "/schema/spidacalc/calc/structure.schema"
			String jsonString = new File("resources/examples/spidacalc/designs/one_of_everything.json").text
		when:
			def report = new Validator().validateAndReport(schema, jsonString)
		then:
			report.isSuccess() // this instance should be valid against the schema
		when:
			new Validator().validate(schema, jsonString)
		then:
			notThrown(JSONServletException) // Should not have thrown a servlet exception
	}

	void "test OneOfEverythingObject validate Map"() {
		setup:
			def schema = "/schema/spidacalc/calc/structure.schema"
			File jsonFile = new File("resources/examples/spidacalc/designs/one_of_everything.json")
			Map jsonObject = new ObjectMapper().readValue(jsonFile, LinkedHashMap)
		when:
			def report = new Validator().validateAndReport(schema, jsonObject)
		then:
			report.isSuccess() // this instance should be valid against the schema
		when:
			new Validator().validate(schema, jsonObject)
		then:
			notThrown(JSONServletException) // Should not have thrown a servlet exception
	}

	void "test an object that is not valid"() {
		setup:
			def schema = "/schema/spidacalc/calc/structure.schema"
			def instance = '{"id":"externalId", "distance":{"unit":"FOOT", "value":10}, "direction":0}'
		when:
			def report = new Validator().validateAndReport(schema, instance)
		then:
			!report.isSuccess() // this instance should NOT be valid against the schema
		when:
			new Validator().validate(schema, instance)
		then:
			thrown(JSONServletException)
	}

	void "test an object that is not valid Map"() {
		setup:
			def schema = "/schema/spidacalc/calc/structure.schema"
			def instance = [id: "externalId", distance: [unit: "FOOT", value: 10], direction: 0]
		when:
			def report = new Validator().validateAndReport(schema, instance)
		then:
			!report.isSuccess() // this instance should NOT be valid against the schema
		when:
			new Validator().validate(schema, instance)
		then:
			thrown(JSONServletException)
	}

	void "test invalid json"() {
		setup:
			def schema = "/schema/spidacalc/calc/structure.schema"
			def badInstance = "{{"
		when:
			new Validator().validate(schema, badInstance)
		then:
			thrown(JSONServletException)
	}

	void "test validateAndReportFromText and validateFromText"() {
		setup:
			def schemaText=  '''
{
  "description": "This is for testing schemas as text instead of as paths to a file.",
  "type": "object",
  "required": [
    "externalId"
  ],
  "properties": {
    "externalId": {
      "description": "Option unique id for tracking within integrator systems.",
      "type": "string"
    }
  }
}
'''
			def invalidInstance = '{"properties":{"externalId":"abc123"}}'
			def validInstance = '{"externalId":"abc123"}'
		when:
			def report = new Validator().validateAndReportFromText(schemaText, validInstance)
		then:
			report.isSuccess()
		when:
			new Validator().validateFromText(schemaText, validInstance)
		then:
			notThrown(JSONServletException)
		when:
			report = new Validator().validateAndReportFromText(schemaText, invalidInstance)
		then:
			!report.isSuccess()
		when:
			new Validator().validateFromText(schemaText, invalidInstance)
		then:
			thrown(JSONServletException)

	}

	void "test ignore additionalValues"() {
		setup:
			def schema = "/schema/spidacalc/calc/structure.schema"
			File jsonFile = new File("resources/examples/spidacalc/designs/one_of_everything.json")
			Map jsonObject = new ObjectMapper().readValue(jsonFile, LinkedHashMap)
			jsonObject.extraValue = "extraValue"
		when:
			def report = new Validator().validateAndReport(schema, jsonObject, true)
		then:
			report.isSuccess() // this instance should be valid against the schema
		when:
			new Validator().validate(schema, jsonObject, true)
		then:
			notThrown(JSONServletException) // Should not have thrown a servlet exception
	}
}
