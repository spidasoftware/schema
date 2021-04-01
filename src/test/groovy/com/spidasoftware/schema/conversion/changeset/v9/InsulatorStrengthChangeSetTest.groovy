/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v9


import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import spock.lang.Specification

class InsulatorStrengthChangeSetTest extends Specification {

	@CompileDynamic
	def "revert client data inside results"() {
		when:
			def stream = InsulatorStrengthChangeSet.getResourceAsStream("/conversions/v8/MultipleStrengthConfiguration.json".toString())
			Map json = new JsonSlurper().parse(stream)
			stream.close()
		then:
			json.clientData.insulators[0].maxCantileverStrength.value == 11120.55403815125
			json.clientData.insulators[0].maxCantileverStrength.unit == "NEWTON"
			json.clientData.insulators[0].maxCompressionStrength.value == 2323.55403815125
			json.clientData.insulators[0].maxCompressionStrength.unit == "NEWTON"
			json.clientData.insulators[0].maxTensionStrength.value == 42342.55403815125
			json.clientData.insulators[0].maxTensionStrength.unit == "NEWTON"
			json.clientData.insulators[0].lineOfActionOffset.value == 111.32132
			json.clientData.insulators[0].lineOfActionOffset.unit == "INCH"
			json.clientData.insulators[0].strength == null
		when:
			new InsulatorStrengthChangeSet().revertResults(json)
		then:
		json.clientData.insulators[0].maxCantileverStrength == null
			json.clientData.insulators[0].maxCompressionStrength == null
			json.clientData.insulators[0].maxTensionStrength == null
			//lowest of the three above
			json.clientData.insulators[0].strength.value == 2323.55403815125d
			json.clientData.insulators[0].strength.unit == "NEWTON"
			json.clientData.insulators[0].lineOfActionOffset == null
	}
}
