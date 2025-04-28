package com.spidasoftware.schema.conversion.changeset.v12

import groovy.json.JsonSlurper
import spock.lang.Specification

class InsulatorTypesChangesetTest extends Specification {

	static InsulatorTypesChangeset changeSet

	def setupSpec() {
		changeSet = new InsulatorTypesChangeset()
	}

	def "revert client data"() {
		setup:
			def stream = InsulatorTypesChangeset.getResourceAsStream("/conversions/v12/insulatorUplift-v12-client.json")
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()
		expect:
			json.analysisCases[1].insulatorTypes != null
		when:
			boolean reverted = changeSet.revertClientData(json)
		then:
			reverted
			!json.analysisCases[1].insulatorTypes
	}

	def "revert project"() {
		setup:
			def stream = InsulatorTypesChangeset.getResourceAsStream("/conversions/v12/insulatorUplift-v12-project.json")
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()
		expect:
			json.clientData.analysisCases[1].insulatorTypes
			json.defaultLoadCases[0].insulatorTypes
			json.leads[0].locations[0].designs[0].analysis[0].analysisCaseDetails.insulatorTypes
		when:
			changeSet.revertProject(json)
		then:
			!json.clientData.analysisCases[1].insulatorTypes
			!json.defaultLoadCases[0].insulatorTypes
			!json.leads[0].locations[0].designs[0].analysis[0].analysisCaseDetails.insulatorTypes
	}

	def "revert results"() {
		setup:
			def stream = InsulatorTypesChangeset.getResourceAsStream("/conversions/v12/insulatorUplift-v12-results.json")
			Map json = new JsonSlurper().parse(stream) as Map
			stream.close()
		expect:
			json.results[0].components[0].uplift
			json.results[0].analysisCaseDetails.insulatorTypes
		when:
			boolean reverted = changeSet.revertResults(json)
		then:
			reverted
			!json.results[0].components[0].uplift
			!json.results[0].analysisCaseDetails.insulatorTypes
	}
}
