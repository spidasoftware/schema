package com.spidasoftware.schema.conversion.changeset

import com.spidasoftware.schema.conversion.changeset.calc.*
import com.spidasoftware.schema.conversion.changeset.v2.PoleLeanChangeSetTest
import com.spidasoftware.schema.validation.Validator
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import spock.lang.Specification

@Log4j
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

	def "test getPossibleCalcVersionsNewestToOldest"() {
		expect:
			ConverterUtils.getPossibleCalcVersionsNewestToOldest() == [7, 6, 5, 4, 3, 2] as LinkedHashSet
	}

	def "pole-lean validation"() {
		expect:
			validateProjectSchema("/conversions/v2/pole-lean.json")
	}

	def "foundation validation"() {
		expect:
			validateProjectSchema("/conversions/v2/foundation.json")
	}

	/*
	Output of "./gradlew printVersion" is:
> Configure project :
SCHEMA_VERSION: 6.0.0-SNAPSHOT

BUILD SUCCESSFUL in 0s
	 */
	def testSchemaVersionMatchesCoverterUtilsVersion() {
		when:
			Process process = "./gradlew printVersion".execute()
			process.waitFor()
			String output = process.text.trim()
			int indexOfMajorVersion = output.indexOf("SCHEMA_VERSION: ") + "SCHEMA_VERSION: ".length()
			String versionStart = output.substring(indexOfMajorVersion, output.length())
		then:
			versionStart.startsWith("${ConverterUtils.currentVersion}")
			ConverterUtils.converters.values().every { Converter converter ->
				versionStart.startsWith("${converter.currentVersion}") && converter.currentVersion == ConverterUtils.currentVersion
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
