/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.validation

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonschema.report.ProcessingReport
import spock.lang.Specification

// copied from ValidatorTest
class NetworkNtJsonValidatorTest extends Specification {

	void "test strict works on nested schemas"() {
		setup:
			String schema = "/schema/spidamin/asset/station.schema"
			String jsonString =""" {
				"strict":false,
				"EXTRA_PROPERTY":"1",
				"dataProviderId":123,
				"geometry":{"type":"Point", "coordinates":[0,0]},
				"assetTypes":["POLE"],
				"stationAssets":[
					{ "EXTRA_PROPERTY":"2", "ownerId":123, "primaryAsset":true, "assetType":"POLE" }
				]
			} """
		when:
			ProcessingReport report = new NetworkNtJsonValidator().validateAndReport(schema, jsonString)
		then:
			report.isSuccess() // this instance should be valid against the schema
		when:
			new NetworkNtJsonValidator().validate(schema, jsonString)
		then:
			notThrown(JSONServletException) // Should not have thrown a servlet exception
	}

	void "test OneOfEverythingObject validate as a String"() {
		setup:
			String schema = "/schema/spidacalc/calc/structure.schema"
			String jsonString = new File("resources/examples/spidacalc/designs/one_of_everything.json").text
		when:
			ProcessingReport report = new NetworkNtJsonValidator().validateAndReport(schema, jsonString)
		then:
			report.isSuccess() // this instance should be valid against the schema
		when:
			new NetworkNtJsonValidator().validate(schema, jsonString)
		then:
			notThrown(JSONServletException) // Should not have thrown a servlet exception
	}

	void "test OneOfEverythingObject validate Map"() {
		setup:
			def schema = "/schema/spidacalc/calc/structure.schema"
			File jsonFile = new File("resources/examples/spidacalc/designs/one_of_everything.json")
			Map jsonObject = new ObjectMapper().readValue(jsonFile, LinkedHashMap)
		when:
			ProcessingReport report = new NetworkNtJsonValidator().validateAndReport(schema, jsonObject)
		then:
			report.isSuccess() // this instance should be valid against the schema
		when:
			new NetworkNtJsonValidator().validate(schema, jsonObject)
		then:
			notThrown(JSONServletException) // Should not have thrown a servlet exception
	}

	void "test an object that is not valid **when strict is true**"() {
		setup:
			String schema = "/schema/spidacalc/calc/structure.schema"
			String instance = '{"id":"externalId", "strict":true, "distance":{"unit":"FOOT", "value":10}, "direction":0}'
		when:
			ProcessingReport report = new NetworkNtJsonValidator().validateAndReport(schema, instance)
		then:
			!report.isSuccess() // this instance should NOT be valid against the schema because strict is true
		when:
			new NetworkNtJsonValidator().validate(schema, instance)
		then:
			thrown(JSONServletException)
	}

	void "test an object that is valid **when strict is false**"() {
		setup:
			String schema = "/schema/spidacalc/calc/structure.schema"
			String instance = '{"id":"externalId", "strict":false, "distance":{"unit":"FOOT", "value":10}, "direction":0}'
		when:
			ProcessingReport report = new NetworkNtJsonValidator().validateAndReport(schema, instance)
		then:
			report.isSuccess() // this instance should be valid against the schema because strict is false
		when:
			new NetworkNtJsonValidator().validate(schema, instance)
		then:
			notThrown(JSONServletException)
	}

	void "test an object that is not valid Map **when strict is true**"() {
		setup:
			String schema = "/schema/spidacalc/calc/structure.schema"
			Map instance = [id: "externalId", strict:true, distance: [unit: "FOOT", value: 10], direction: 0]
		when:
			ProcessingReport report = new NetworkNtJsonValidator().validateAndReport(schema, instance)
		then:
			!report.isSuccess() // this instance should NOT be valid against the schema because strict is true
		when:
			new NetworkNtJsonValidator().validate(schema, instance)
		then:
			thrown(JSONServletException)
	}

	void "test an object that is not valid Map **when strict is false**"() {
		setup:
			String schema = "/schema/spidacalc/calc/structure.schema"
			Map instance = [id: "externalId", strict:false, distance: [unit: "FOOT", value: 10], direction: 0]
		when:
			ProcessingReport report = new NetworkNtJsonValidator().validateAndReport(schema, instance)
		then:
			report.isSuccess() // this instance should be valid against the schema because strict is false
		when:
			new NetworkNtJsonValidator().validate(schema, instance)
		then:
			notThrown(JSONServletException)
	}

	void "test invalid json"() {
		setup:
			String schema = "/schema/spidacalc/calc/structure.schema"
			String badInstance = "{{"
		when:
			new NetworkNtJsonValidator().validate(schema, badInstance)
		then:
			thrown(JSONServletException)
	}

	void "test validateAndReportFromText and validateFromText"() {
		setup:
			String schemaText=  '''
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
			String invalidInstance = '{"properties":{"externalId":"abc123"}}'
			String validInstance = '{"externalId":"abc123"}'
		when:
			ProcessingReport report = new NetworkNtJsonValidator().validateAndReportFromText(schemaText, validInstance)
		then:
			report.isSuccess()
		when:
			new NetworkNtJsonValidator().validateFromText(schemaText, validInstance)
		then:
			notThrown(JSONServletException)
		when:
			report = new NetworkNtJsonValidator().validateAndReportFromText(schemaText, invalidInstance)
		then:
			!report.isSuccess()
		when:
			new NetworkNtJsonValidator().validateFromText(schemaText, invalidInstance)
		then:
			thrown(JSONServletException)
	}
}
