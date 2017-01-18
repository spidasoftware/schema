package com.spidasoftware.schema.conversion.changeset

import com.spidasoftware.schema.validation.Validator
import com.spidasoftware.schema.conversion.changeset.v2.PoleLeanChangeSetTest
import groovy.util.logging.Log4j
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

/**
 * Created: 11/26/14
 * Copyright SPIDAWeb
 */
@Log4j
class ConverterIntegrationTest extends Specification {

	def "pole-lean"() {
		def leanStream = PoleLeanChangeSetTest.getResourceAsStream("/conversions/v2/pole-lean.json")
		def json = new JsonSlurper().parse(leanStream)

		when:
			AbstractConverter.convertJSON(json, 1)

			def pole = json.leads.first().locations.first().designs.first().structure.pole
		then:
			pole.containsKey("leanAngle") == false
			pole.containsKey("leanDirection") == false
			1 == json.version
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
