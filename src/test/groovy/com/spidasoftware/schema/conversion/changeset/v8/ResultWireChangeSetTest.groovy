/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v8

import spock.lang.Specification

class ResultWireChangeSetTest extends Specification {

	void "revertResults" (){
		setup:
			def json = [results:[[components:[[wires:[[tensionMethod:"NONLINEAR_STRESS_STRAIN"]]]]]]]
		when:
			new ResultsWireChangeSet().revertResults(json)
		then:
			json.results[0].components[0].wires[0].tensionMethod == "DYNAMIC"
	}

}