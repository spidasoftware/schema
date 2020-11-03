/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v8

import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import spock.lang.Specification

class AdvancedWireChangeSetTest extends Specification {

	@CompileDynamic
	def "revert client data"() {
		def stream = AdvancedWireChangeSet.getResourceAsStream("/conversions/v8/AdvancedWireChangeSet.json".toString())
		Map json = new JsonSlurper().parse(stream)
		stream.close()
		when:
			new AdvancedWireChangeSet().revertClientData(json)
		then:
			json.wires[0].calculation == "DYNAMIC"
			json.wires[0].modulus.value == 7.451508944591707E10
			json.wires[0].modulus.unit == "PASCAL"
			json.wires[0].expansionCoefficient.value == 1.8821596113809853E-5
			json.wires[0].expansionCoefficient.unit == "PER_CELSIUS"
			json.wires[0].conductorProperties == "TOTAL"
			json.wires[0].outerModulus == null
			json.wires[0].coreModulus == null
			json.wires[0].outerExpansionCoefficient == null
			json.wires[0].coreExpansionCoefficient == null
			json.wires[0].stressStrainPolynomials == null
			json.wires[0].creepPolynomials == null

			//convert imperial to metric values
			json.wires[1].calculation == "DYNAMIC"
			json.wires[1].modulus.value == 1.08075E7
			json.wires[1].modulus.unit == "PASCAL"
			json.wires[1].expansionCoefficient.value == 1.0456442285449919E-5
			json.wires[1].expansionCoefficient.unit == "PER_CELSIUS"
			json.wires[1].conductorProperties == "TOTAL"
			json.wires[1].outerModulus == null
			json.wires[1].coreModulus == null
			json.wires[1].outerExpansionCoefficient == null
			json.wires[1].coreExpansionCoefficient == null
			json.wires[1].stressStrainPolynomials == null
			json.wires[1].creepPolynomials == null
	}

	@CompileDynamic
	def "revert client data inside results"() {
		def stream = AdvancedWireChangeSet.getResourceAsStream("/conversions/v8/AdvancedWireChangeSet.json".toString())
		Map json = new JsonSlurper().parse(stream)
		stream.close()
		when:
			new AdvancedWireChangeSet().revertResults(json)
		then:
			json.clientData.wires[0].calculation == "DYNAMIC"
			json.clientData.wires[0].modulus.value == 5.653700980398057E10
			json.clientData.wires[0].modulus.unit == "PASCAL"
			json.clientData.wires[0].expansionCoefficient.value == 2.304E-5
			json.clientData.wires[0].expansionCoefficient.unit == "PER_CELSIUS"
			json.clientData.wires[0].conductorProperties == "TOTAL"
			json.clientData.wires[0].outerModulus == null
			json.clientData.wires[0].coreModulus == null
			json.clientData.wires[0].outerExpansionCoefficient == null
			json.clientData.wires[0].coreExpansionCoefficient == null
			json.clientData.wires[0].stressStrainPolynomials == null
			json.clientData.wires[0].creepPolynomials == null
	}
}
