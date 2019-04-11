/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v2

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Log4j

/**
 * Changeset to handle new Pole Lean fields in /v1/schema/spidacalc/calc/pole.schema . These are optional fields, so
 * they are not added on apply, but they must be removed on revert
 */
@Log4j
class PoleLeanChangeSet extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map json) throws ConversionException {
		// we don't need to change anything to the json -- this
	}

	@Override
	void revertDesign(Map design) throws ConversionException {
		def structure = design.get("structure")
		if (structure) {
			def pole = structure.get("pole")
			if (pole.containsKey("leanAngle")) {
				pole.remove("leanAngle")
			}
			if (pole.containsKey("leanDirection")) {
				pole.remove("leanDirection")
			}
		}
	}
}
