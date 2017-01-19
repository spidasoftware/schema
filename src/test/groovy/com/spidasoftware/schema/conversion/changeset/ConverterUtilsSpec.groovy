package com.spidasoftware.schema.conversion.changeset

import com.spidasoftware.schema.validation.Validator
import com.spidasoftware.schema.conversion.changeset.v2.PoleLeanChangeSetTest
import groovy.util.logging.Log4j
import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

@Log4j
class ConverterUtilsSpec extends Specification {

	def "pole-lean"() {
		setup:
			def leanStream = PoleLeanChangeSetTest.getResourceAsStream("/conversions/v2/pole-lean.json")
			JSONObject projectJSON = new JsonSlurper().parse(leanStream)
			JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
			locationJSON.schema = "/v1/schema/spidacalc/calc/location.schema"
			locationJSON.version = 2
			JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])
			designJSON.schema = "/v1/schema/spidacalc/calc/design.schema"
			designJSON.version = 2

		when: "projectJSON"
			ConverterUtils.convertJSON(projectJSON, 1)
			def pole = projectJSON.leads.first().locations.first().designs.first().structure.pole
		then:
			pole.containsKey("leanAngle") == false
			pole.containsKey("leanDirection") == false
			1 == projectJSON.version
		when: "locationJSON"
			ConverterUtils.convertJSON(locationJSON, 1)
			pole = locationJSON.designs.first().structure.pole
		then:
			pole.containsKey("leanAngle") == false
			pole.containsKey("leanDirection") == false
			1 == locationJSON.version
		when: "designJSON"
			ConverterUtils.convertJSON(designJSON, 1)
			pole = designJSON.structure.pole
		then:
			pole.containsKey("leanAngle") == false
			pole.containsKey("leanDirection") == false
			1 == designJSON.version
	}

	def "pole-lean validation"() {
		expect:
			validateProjectSchema("/conversions/v2/pole-lean.json")
	}

	def "foundation validation"() {
		expect:
			validateProjectSchema("/conversions/v2/foundation.json")
	}

	private boolean validateProjectSchema(String jsonPath) {
		def inputStream = PoleLeanChangeSetTest.getResourceAsStream(jsonPath)
		String json = inputStream.text
		String schema = "/schema/spidacalc/calc/project.schema"
		def report = new Validator().validateAndReport(schema, json)

		log.info(json)

		return report.isSuccess()
	}
}
