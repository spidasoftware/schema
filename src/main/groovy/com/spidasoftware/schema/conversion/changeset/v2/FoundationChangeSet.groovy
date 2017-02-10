package com.spidasoftware.schema.conversion.changeset.v2

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONObject

/**
 * Removes foundation and ground water level from pole json.
 */
class FoundationChangeSet extends AbstractDesignChangeset {
	@Override
	String getSchemaPath() {
		"/schema/spidacalc/calc/project.schema"
	}

	@Override
	void applyToDesign(JSONObject json) throws ConversionException {
		//nothing needed as the new values are optional
	}

	@Override
	void revertDesign(JSONObject design) throws ConversionException {
		def structure = design.get("structure")
		if (structure) {
			JSONObject pole = structure.get("pole")
			if (pole.has("foundation")) {
				pole.remove("foundation")
			}
			if (structure.has("foundations")) {
				structure.remove("foundations")
			}
		}
	}
}
