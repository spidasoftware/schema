package com.spidasoftware.schema.conversion.changeset

import com.spidasoftware.schema.conversion.changeset.calc.*
import spock.lang.Specification

class ConverterTest extends Specification {

    Map json = [:]
	ChangeSet oneToTwo = GroovyMock(){ getClass()>>ChangeSet }
	ChangeSet twoToThreeA = GroovyMock(){ getClass()>>ChangeSet }
	ChangeSet twoToThreeB = GroovyMock(){ getClass()>>ChangeSet }
	ChangeSet fiveToOne = GroovyMock(){ getClass()>>ChangeSet }
	TreeMap versions = [2:[oneToTwo], 3: [twoToThreeA, twoToThreeB], 5: [fiveToOne]]
	CalcProjectConverter projectConverter = new CalcProjectConverter(versions: versions)
	CalcLocationConverter locationConverter = new CalcLocationConverter(versions: versions)
	CalcDesignConverter designConverter = new CalcDesignConverter(versions: versions)

	def "Convert null-2"() {
		when: "projectConverter"
			json.put("version", null)
			projectConverter.convert(json, 2)
		then:
			1 * oneToTwo.applyToProject(json)
			0 * _._ // no others should be applied
		when: "locationConverter"
			json.put("version", null)
			locationConverter.convert(json, 2)
		then:
			1 * oneToTwo.applyToLocation(json)
			0 * _._ // no others should be applied
		when: "designConverter"
			json.put("version", null)
			designConverter.convert(json, 2)
		then:
			1 * oneToTwo.applyToDesign(json)
			0 * _._ // no others should be applied
	}

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
			0 * _.applyToProject(_) // no others should be applied
			0 * _.revertProject(_) // no others should be applied
		when: "locationConverter"
			json.put("version", 3)
			locationConverter.convert(json, 2)
		then:
			1 * twoToThreeB.revertLocation(json)
		then:
			1 * twoToThreeA.revertLocation(json)
			0 * _.applyToLocation(_) // no others should be applied
			0 * _.revertLocation(_) // no others should be applied
		when: "designConverter"
			json.put("version", 3)
			designConverter.convert(json, 2)
		then:
			1 * twoToThreeB.revertDesign(json)
		then:
			1 * twoToThreeA.revertDesign(json)
			0 * _.applyToDesign(_) // no others should be applied
			0 * _.revertDesign(_) // no others should be applied
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
			0 * _.applyToProject(_) // no others should be applied
			0 * _.revertProject(_) // no others should be applied
		when: "locationConverter"
			json.put("version", 3)
			locationConverter.convert(json, 1)
		then:
			1*twoToThreeB.revertLocation(json)
		then:
			1*twoToThreeA.revertLocation(json)
		then:
			1*oneToTwo.revertLocation(json)
			0 * _.applyToLocation(_) // no others should be applied
			0 * _.revertLocation(_) // no others should be applied
		when: "designConverter"
			json.put("version", 3)
			designConverter.convert(json, 1)
		then:
			1*twoToThreeB.revertDesign(json)
		then:
			1*twoToThreeA.revertDesign(json)
		then:
			1*oneToTwo.revertDesign(json)
			0 * _.applyToDesign(_) // no others should be applied
			0 * _.revertDesign(_) // no others should be applied
	}

	def "convert removes version"() {
		setup:
			json = [version: 5]
		when: "projectConverter"
			projectConverter.convert(json, 1)
		then:
			json.version == 1
		when: "locationConverter"
			json = [version: 5]
			locationConverter.convert(json, 1)
		then:
			!json.containsKey("version")
		when: "locationConverter"
			json = [version: 5]
			designConverter.convert(json, 1)
		then:
			!json.containsKey("version")
	}

	def "convert adds version"() {
		setup:
			json = [:]
		when: "projectConverter"
			projectConverter.convert(json, 4)
		then:
			json.version == 4
		when: "locationConverter"
			json = [:]
			locationConverter.convert(json, 4)
		then:
			json.version == 4
		when: "locationConverter"
			json = [:]
			designConverter.convert(json, 4)
		then:
			json.version == 4
	}
}
