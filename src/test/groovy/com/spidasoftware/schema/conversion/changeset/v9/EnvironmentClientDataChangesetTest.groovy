/*
 * ©2009-2021 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v9

import groovy.json.JsonSlurper
import spock.lang.Specification

class EnvironmentClientDataChangesetTest extends Specification {


	def "apply to client data"() {
		setup:
		Map json = [:]
		when: "apply up conversion to client data"
			new EnvironmentClientDataChangeset().applyToClientData(json)
		then: "old defaults are added to client data"
			json.environments.size == 15
			json.environments[0].name == "Alley"
			json.environments[0].description == "N/A"
			json.environments[1].name == "Commercial Driveway"
			json.environments[1].description == "N/A"
			json.environments[2].name == "Farm"
			json.environments[2].description == "N/A"
			json.environments[3].name == "Highway"
			json.environments[3].description == "N/A"
			json.environments[4].name == "Obstructed Parallel To Street"
			json.environments[4].description == "N/A"
			json.environments[5].name == "Parallel To Street"
			json.environments[5].description == "N/A"
			json.environments[6].name == "Parking Lot"
			json.environments[6].description == "N/A"
			json.environments[7].name == "Pedestrian"
			json.environments[7].description == "N/A"
			json.environments[8].name == "Railroad"
			json.environments[8].description == "N/A"
			json.environments[9].name == "Residential Driveway"
			json.environments[9].description == "N/A"
			json.environments[10].name == "Rural"
			json.environments[10].description == "N/A"
			json.environments[11].name == "Street"
			json.environments[11].description == "N/A"
			json.environments[12].name == "Unlikely Parallel To Street"
			json.environments[12].description == "N/A"
			json.environments[13].name == "Water With Sailboats"
			json.environments[13].description == "N/A"
			json.environments[14].name == "Water Without Sailboats"
			json.environments[14].description == "N/A"
	}

	def "apply to project"() {
		setup:
		def stream = EnvironmentClientDataChangeset.getResourceAsStream("/conversions/v9/environment.v8.project.json".toString())
		Map json = new JsonSlurper().parse(stream)
		stream.close()
		when: "apply up conversion to project"
			new EnvironmentClientDataChangeset().applyToProject(json)
		then: "Add project environments to clientData"
			json.clientData.environments.size == 4
			json.clientData.environments[0].name == "Commercial Driveway"
			json.clientData.environments[0].description == "N/A"
			json.clientData.environments[1].name == "Obstructed Parallel To Street"
			json.clientData.environments[1].description == "N/A"
			json.clientData.environments[2].name == "Pedestrian"
			json.clientData.environments[2].description == "N/A"
			json.clientData.environments[3].name == "Water Without Sailboats"
			json.clientData.environments[3].description == "N/A"
		then: "Pretty print all environments in project"
			json.leads[0].locations[0].designs[0].structure.pole.environment == "None"
			json.leads[0].locations[0].designs[0].structure.wireEndPoints[0].environment == "Commercial Driveway"
			json.leads[0].locations[0].designs[0].structure.wireEndPoints[1].environment == "Obstructed Parallel To Street"
			json.leads[0].locations[0].designs[0].structure.spanPoints[0].environment == "Pedestrian"
			json.leads[0].locations[0].designs[0].structure.spanPoints[1].environment == "Water Without Sailboats"
	}

	def "apply to results"() {
		when:
		def stream = EnvironmentClientDataChangeset.getResourceAsStream("/conversions/v9/environment.v8.results.json".toString())
		Map json = new JsonSlurper().parse(stream)
		stream.close()
		new EnvironmentClientDataChangeset().applyToResults(json)
		then: "Add analyzedStructure environments to clientData"
			json.clientData.environments.size == 4
			json.clientData.environments[0].name == "Commercial Driveway"
			json.clientData.environments[0].description == "N/A"
			json.clientData.environments[1].name == "Obstructed Parallel To Street"
			json.clientData.environments[1].description == "N/A"
			json.clientData.environments[2].name == "Pedestrian"
			json.clientData.environments[2].description == "N/A"
			json.clientData.environments[3].name == "Water Without Sailboats"
			json.clientData.environments[3].description == "N/A"
		then: "Pretty print all environments in analyzedStructure"
			json.analyzedStructure.pole.environment == "None"
			json.analyzedStructure.wireEndPoints[0].environment == "Obstructed Parallel To Street"
			json.analyzedStructure.wireEndPoints[1].environment == "Commercial Driveway"
			json.analyzedStructure.spanPoints[0].environment == "Water Without Sailboats"
			json.analyzedStructure.spanPoints[1].environment == "Pedestrian"
	}

	def "revert client data"() {
		setup:
		def stream = EnvironmentClientDataChangeset.getResourceAsStream("/conversions/v9/environment.v9.client.json".toString())
		Map json = new JsonSlurper().parse(stream)
		stream.close()
		expect:
			json.environments.size == 4
			json.environments[0].name == "Street"
			json.environments[0].description == "Street"
			json.environments[1].name == "Parking Lot"
			json.environments[1].description == "Parking Lot"
			json.environments[2].name == "Wet Foundation"
			json.environments[2].description == "Foundation is damp"
			json.environments[3].name == "Test"
			json.environments[3].description == "I am writing a really long description for a test. This note does not make any sense."
		when: "apply up conversion to client data"
			boolean reverted = new EnvironmentClientDataChangeset().revertClientData(json)
		then: "remove environments"
			!json.containsKey("environment")
			reverted
	}

	def "revert project"() {
		setup:
		def stream = EnvironmentClientDataChangeset.getResourceAsStream("/conversions/v9/environment.v9.project.json".toString())
		Map json = new JsonSlurper().parse(stream)
		stream.close()
		expect:
			json.clientData.environments.size == 4
			json.clientData.environments[0].name == "Street"
			json.clientData.environments[0].description == "Street"
			json.clientData.environments[1].name == "Parking Lot"
			json.clientData.environments[1].description == "Parking Lot"
			json.clientData.environments[2].name == "Wet Foundation"
			json.clientData.environments[2].description == "Foundation is damp"
			json.clientData.environments[3].name == "Test"
			json.clientData.environments[3].description == "I am writing a really long description for a test. This note does not make any sense."
			json.leads[0].locations[0].designs[0].structure.pole.environment == "Street"
			json.leads[0].locations[0].designs[0].structure.wireEndPoints[0].environment == "None"
			json.leads[0].locations[0].designs[0].structure.wireEndPoints[1].environment == "Parking Lot"
			json.leads[0].locations[0].designs[0].structure.spanPoints[0].environment == "Wet Foundation"
			json.leads[0].locations[0].designs[0].structure.spanPoints[1].environment == "Test"
		when: "apply up conversion to project"
			new EnvironmentClientDataChangeset().revertProject(json)
		then: "remove environments"
			!json.clientData.containsKey("environment")
		then:
			json.leads[0].locations[0].designs[0].structure.pole.environment == "STREET"
			json.leads[0].locations[0].designs[0].structure.wireEndPoints[0].environment == "NONE"
			json.leads[0].locations[0].designs[0].structure.wireEndPoints[1].environment == "PARKING_LOT"
			json.leads[0].locations[0].designs[0].structure.spanPoints[0].environment == "NONE"
			json.leads[0].locations[0].designs[0].structure.spanPoints[1].environment == "NONE"
	}

	def "revert client data in results"() {
		setup:
		def stream = EnvironmentClientDataChangeset.getResourceAsStream("/conversions/v9/environment.v9.results.json".toString())
		Map json = new JsonSlurper().parse(stream)
		stream.close()
		expect:
			json.clientData.environments.size == 3
			json.clientData.environments[0].name == "Street"
			json.clientData.environments[0].description == "N/A"
			json.clientData.environments[1].name == "Parking Lot"
			json.clientData.environments[1].description == "N/A"
			json.clientData.environments[2].name == "Test"
			json.clientData.environments[2].description == "I am writing a really long description for a test. This note does not make any sense."
		when:
			new EnvironmentClientDataChangeset().revertResults(json)
		then: "clientData in results has no environments"
			json.clientData.environments == null
		then:
			json.analyzedStructure.pole.environment == "NONE"
			json.analyzedStructure.wireEndPoints[0].environment == "NONE"
			json.analyzedStructure.wireEndPoints[1].environment == "STREET"
			json.analyzedStructure.spanPoints[0].environment == "PARKING_LOT"
			json.analyzedStructure.spanPoints[1].environment == "STREET"
	}
}
