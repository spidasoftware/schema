package com.spidasoftware.schema.conversion

import groovy.transform.CompileStatic

/**
 * Represents a Result that exists in SPIDAdb
 */
@CompileStatic
class SpidaDBResult extends AbstractSpidaDBComponent {
	SpidaDBResult(Map json){
		super(json)
	}

	@Override
	String toString(){
		return getName() ?: "Result"
	}

	@Override
	final Map getCalcJSON() {
		return (getMap().get(getCalcJSONName()) as Map).asImmutable()
	}

	@Override
	String getCalcJSONName() {
		return 'calcResult'
	}

	@Override
	String getName(){
		return "Result"
	}
}
