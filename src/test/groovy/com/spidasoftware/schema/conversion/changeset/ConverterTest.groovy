package com.spidasoftware.schema.conversion.changeset

import groovy.util.logging.Log4j
import spock.lang.Specification

/**
 * Created: 11/17/14
 * Copyright SPIDAWeb
 */
@Log4j

class ConverterTest extends Specification {

    Map json = [:]
	ChangeSet oneToTwo = GroovyMock()
	ChangeSet twoToThreeA = GroovyMock()
	ChangeSet twoToThreeB = GroovyMock()
	TreeMap versions = [2:[oneToTwo], 3: [twoToThreeA, twoToThreeB]]
	ProjectConverter projectConverter = new ProjectConverter(versions: versions)
	LocationConverter locationConverter = new LocationConverter(versions: versions)
	DesignConverter designConverter = new DesignConverter(versions: versions)

	def "Convert1-2"() {
		setup:
			json.put("version", 1)
		when: "projectConverter"
			projectConverter.convert(json, 2)
		then:
			1 * oneToTwo.applyToProject(json)
			0 * _._ // no others should be applied
		when: "locationConverter"
			json.put("version", 1)
			locationConverter.convert(json, 2)
		then:
			1 * oneToTwo.applyToLocation(json)
			0 * _._ // no others should be applied
		when: "designConverter"
			json.put("version", 1)
			designConverter.convert(json, 2)
		then:
			1 * oneToTwo.applyToDesign(json)
			0 * _._ // no others should be applied
	}
	def "Convert1-3"() {
		setup:
			json.put("version", 1)
		when: "projectConverter"
			projectConverter.convert(json, 3)
		then:
			1 * oneToTwo.applyToProject(json)
		then:
			1 * twoToThreeA.applyToProject(json)
		then:
			1 * twoToThreeB.applyToProject(json)
			0 * _._ // no others should be applied
		when: "locationConverter"
			json.put("version", 1)
			locationConverter.convert(json, 3)
		then:
			1 * oneToTwo.applyToLocation(json)
		then:
			1 * twoToThreeA.applyToLocation(json)
		then:
			1 * twoToThreeB.applyToLocation(json)
			0 * _._ // no others should be applied
		when: "designConverter"
			json.put("version", 1)
			designConverter.convert(json, 3)
		then:
			1 * oneToTwo.applyToDesign(json)
		then:
			1 * twoToThreeA.applyToDesign(json)
		then:
			1 * twoToThreeB.applyToDesign(json)
			0 * _._ // no others should be applied
	}
	def "convert 2-3"() {
		setup:
			json.put("version", 2)
		when: "projectConverter"
			projectConverter.convert(json, 3)
		then:
			1 * twoToThreeA.applyToProject(json)
		then:
			1 * twoToThreeB.applyToProject(json)
			0 * _._ // no others should be applied
		when: "locationConverter"
			json.put("version", 2)
			locationConverter.convert(json, 3)
		then:
			1 * twoToThreeA.applyToLocation(json)
		then:
			1 * twoToThreeB.applyToLocation(json)
			0 * _._ // no others should be applied
		when: "designConverter"
			json.put("version", 2)
			designConverter.convert(json, 3)
		then:
			1 * twoToThreeA.applyToDesign(json)
		then:
			1 * twoToThreeB.applyToDesign(json)
			0 * _._ // no others should be applied
	}

	def "convert 3-2"() {
		setup:
			json.put("version", 3)
		when: "projectConverter"
			projectConverter.convert(json, 2)
		then:
			1 * twoToThreeB.revertProject(json)
		then:
			1 * twoToThreeA.revertProject(json)
			0 * _._ // no others should be applied
		when: "locationConverter"
			json.put("version", 3)
			locationConverter.convert(json, 2)
		then:
			1 * twoToThreeB.revertLocation(json)
		then:
			1 * twoToThreeA.revertLocation(json)
			0 * _._ // no others should be applied
		when: "designConverter"
			json.put("version", 3)
			designConverter.convert(json, 2)
		then:
			1 * twoToThreeB.revertDesign(json)
		then:
			1 * twoToThreeA.revertDesign(json)
			0 * _._ // no others should be applied
	}

	def "convert 3-1"() {
		setup:
			json.put("version", 3)
		when: "projectConverter"
			projectConverter.convert(json, 1)
		then:
			1*twoToThreeB.revertProject(json)
		then:
			1*twoToThreeA.revertProject(json)
		then:
			1*oneToTwo.revertProject(json)
			0 * _._ // no others should be applied
		when: "locationConverter"
			json.put("version", 3)
			locationConverter.convert(json, 1)
		then:
			1*twoToThreeB.revertLocation(json)
		then:
			1*twoToThreeA.revertLocation(json)
		then:
			1*oneToTwo.revertLocation(json)
			0 * _._ // no others should be applied
		when: "designConverter"
			json.put("version", 3)
			designConverter.convert(json, 1)
		then:
			1*twoToThreeB.revertDesign(json)
		then:
			1*twoToThreeA.revertDesign(json)
		then:
			1*oneToTwo.revertDesign(json)
			0 * _._ // no others should be applied

	}
}
