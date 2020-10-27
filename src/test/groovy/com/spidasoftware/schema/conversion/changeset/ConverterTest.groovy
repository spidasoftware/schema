package com.spidasoftware.schema.conversion.changeset

import com.spidasoftware.schema.conversion.changeset.calc.*
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import com.spidasoftware.schema.conversion.changeset.client.ClientDataConverter
import spock.lang.Specification

class ConverterTest extends Specification {

	Map json = [:]
	CalcProjectChangeSet oneToTwo = GroovyMock(){ getClass()>>CalcProjectChangeSet }
	CalcProjectChangeSet twoToThreeA = GroovyMock(){ getClass()>>CalcProjectChangeSet }
	CalcProjectChangeSet twoToThreeB = GroovyMock(){ getClass()>>CalcProjectChangeSet }
	CalcProjectChangeSet fiveToOne = GroovyMock(){ getClass()>>CalcProjectChangeSet }
	AbstractClientDataChangeSet clientDataSevenToEight = GroovyMock(){ getClass()>> AbstractClientDataChangeSet }

	TreeMap versions = [2:[oneToTwo], 3: [twoToThreeA, twoToThreeB], 5: [fiveToOne], 8: [clientDataSevenToEight]]
	CalcProjectConverter projectConverter = new CalcProjectConverter(versions: versions)
	CalcLocationConverter locationConverter = new CalcLocationConverter(versions: versions)
	CalcDesignConverter designConverter = new CalcDesignConverter(versions: versions)
	ClientDataConverter clientDataConverter = new ClientDataConverter(versions: [8: [clientDataSevenToEight]])

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

	def "convert sets version"() {
		setup:
			json = [version: 5, leads: [[locations: [[version: 5, designs: [[version: 5]]]]]]]
		when: "projectConverter"
			projectConverter.convert(json, 4)
		then:
			json.version == 4
			json.leads.first().locations.first().version == 4
			json.leads.first().locations.first().designs.first().version == 4
		when: "locationConverter"
			json = [version: 5, designs: [[version: 5]]]
			locationConverter.convert(json, 4)
		then:
			json.version == 4
			json.designs.first().version == 4
		when: "designConverter"
			json = [:]
			designConverter.convert(json, 4)
		then:
			json.version == 4
	}

	def "convert doesn't set version when version is less than 4"() {
		setup:
			json = [version: 5, leads: [[locations: [[designs: [[:]]]]]]]
		when: "projectConverter"
			projectConverter.convert(json, 3)
		then:
			json.version == 3
			!json.leads.first().locations.first().containsKey("version")
			!json.leads.first().locations.first().designs.first().containsKey("version")
		when: "locationConverter"
			json = [designs: [[:]]]
			locationConverter.convert(json, 3)
		then:
			!json.containsKey("version")
			!json.designs.first().containsKey("version")
		when: "designConverter"
			json = [:]
			designConverter.convert(json, 3)
		then:
			!json.containsKey("version")
	}

	def "update version throughout project tree"() {
		Map projectJSON = [version: 7, clientData: [version : 7],
		leads: [[
		        locations: [
		                [designs: [[version: 7, analysisDetails: [detailedResults: [clientData: [version: 7]]]]],
		                version: 7]
		        ]
		]]]
		when:
			projectConverter.convert(projectJSON, 8)
		then:
			projectJSON.version == 8
			projectJSON.clientData.version == 8
			projectJSON.leads[0].locations[0].version == 8
			projectJSON.leads[0].locations[0].designs[0].version == 8
			projectJSON.leads[0].locations[0].designs[0].analysisDetails.detailedResults.clientData.version == 8
	}

	def "convert 8 to 7"() {
		setup:
		Map projectJSON = [version: 8, clientData: [:]]
		Map clientDataJSON = [version: 8]
		when: "project"
			projectConverter.convert(projectJSON, 7)
		then:
			1 * clientDataSevenToEight.revertProject(_)
			0 * clientDataSevenToEight.applyToProject(_)
		when: "clientData"
			clientDataConverter.convert(clientDataJSON, 7)
		then:
			1 * clientDataSevenToEight.revertClientData(clientDataJSON)
			0 * clientDataSevenToEight.applyToClientData(clientDataJSON)
	}

	def "convert 7 to 8"() {
		setup:
		Map projectJSON = [version: 7, clientData: [:]]
		Map clientDataJSON = [version: 7]
		when: "project"
			projectConverter.convert(projectJSON, 8)
		then:
			0 * clientDataSevenToEight.revertProject(_)
			1 * clientDataSevenToEight.applyToProject(_)
		when: "clientData"
			clientDataConverter.convert(clientDataJSON, 8)
		then:
			0 * clientDataSevenToEight.revertClientData(clientDataJSON)
			1 * clientDataSevenToEight.applyToClientData(clientDataJSON)
	}
}
