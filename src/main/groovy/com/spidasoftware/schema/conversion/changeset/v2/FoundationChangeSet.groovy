package com.spidasoftware.schema.conversion.changeset.v2

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException


/**
 * Removes foundation and ground water level from pole json.
 */
class FoundationChangeSet extends AbstractCalcDesignChangeset {


	@Override
	void applyToDesign(Map json) throws ConversionException {
		//nothing needed as the new values are optional
	}

	@Override
	void revertDesign(Map design) throws ConversionException {
		def structure = design.get("structure")
		if (structure) {
			Map pole = structure.get("pole")
			if (pole.containsKey("foundation")) {
				pole.remove("foundation")
			}
			if (structure.containsKey("foundations")) {
				structure.remove("foundations")
			}
		}
	}
}
