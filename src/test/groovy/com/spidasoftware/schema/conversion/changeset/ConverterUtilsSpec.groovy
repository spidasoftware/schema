/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset

import com.spidasoftware.schema.conversion.changeset.calc.CalcDesignConverter
import com.spidasoftware.schema.conversion.changeset.calc.CalcLocationConverter
import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectConverter
import com.spidasoftware.schema.conversion.changeset.v2.PoleLeanChangeSetTest
import com.spidasoftware.schema.validation.Validator
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import spock.lang.Specification

@Slf4j
class ConverterUtilsSpec extends Specification {

	def "pole-lean"() {
		setup:
			def leanStream = PoleLeanChangeSetTest.getResourceAsStream("/conversions/v2/pole-lean.json")
		    Map projectJSON = new JsonSlurper().parse(leanStream)
		    Map locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
			locationJSON.schema = "/v1/schema/spidacalc/calc/location.schema"
			locationJSON.version = 2
		    Map designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])
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
			2 == locationJSON.version
		when: "designJSON"
			ConverterUtils.convertJSON(designJSON, 1)
			pole = designJSON.structure.pole
		then:
			pole.containsKey("leanAngle") == false
			pole.containsKey("leanDirection") == false
			2 == designJSON.version
	}

	def "test getConverterInstance"() {
		expect:
			ConverterUtils.getConverterInstance("/schema/spidacalc/calc/project.schema") instanceof CalcProjectConverter
			ConverterUtils.getConverterInstance("/schema/spidacalc/calc/location.schema") instanceof CalcLocationConverter
			ConverterUtils.getConverterInstance("/schema/spidacalc/calc/design.schema") instanceof CalcDesignConverter
	}

	def "test getPossibleVersionsNewestToOldest"() {
		expect:
			ConverterUtils.getPossibleVersionsNewestToOldest() == [14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2] as LinkedHashSet
	}

	def "structural truss round trip between v14 and v13"() {
		setup:
			Map clientJSON = new JsonSlurper().parse(ConverterUtilsSpec.getResourceAsStream("/conversions/v14/structuralTruss-v14-clientfile.json")) as Map
			Map projectJSON = new JsonSlurper().parse(ConverterUtilsSpec.getResourceAsStream("/conversions/v14/structuralTruss-v14-project.json")) as Map
		when: "client data to v13"
			ConverterUtils.convertJSON(clientJSON, 13)
		then:
			clientJSON.version == 13
			clientJSON.trusses.every { Map truss -> !truss.containsKey("modelingType") && !truss.containsKey("momentCapacityAtHeights") }
			clientJSON.trusses.every { Map truss -> truss.momentCapacity != null }
			!clientJSON.containsKey("hash")
		when: "back to the current version"
			ConverterUtils.convertJSON(clientJSON)
		then: "a v13 truss stays a moment capacity truss (modelingType absent)"
			clientJSON.version == 14
			clientJSON.trusses.every { Map truss -> !truss.containsKey("modelingType") }
			new Validator().validateAndReport("/schema/spidacalc/client/data.schema", clientJSON).isSuccess()
		when: "project to v13"
			ConverterUtils.convertJSON(projectJSON, 13)
		then:
			projectJSON.version == 13
			!projectJSON.clientData.trusses[0].containsKey("modelingType")
			projectJSON.leads[0].locations[0].designs[0].analysisCurrent == false
			!projectJSON.leads[0].locations[0].designs[0].analysisDetails.detailedResults.clientData.trusses[0].containsKey("width")
	}

	def "pole-lean validation"() {
		expect:
			validateProjectSchema("/conversions/v2/pole-lean.json")
	}

	def "foundation validation"() {
		expect:
			validateProjectSchema("/conversions/v2/foundation.json")
	}

	def testSchemaVersionMatchesConverterUtilsVersion() {
		when:
			Properties props = new Properties()
			ConverterUtilsSpec.getResourceAsStream("/schema-version.properties").withStream { props.load(it) }
			String schemaVersion = props.getProperty("version")
		then:
			schemaVersion.startsWith("${ConverterUtils.currentVersion}")
			ConverterUtils.converters.values().every { Converter converter ->
				schemaVersion.startsWith("${converter.currentVersion}") && converter.currentVersion == ConverterUtils.currentVersion
			}
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
