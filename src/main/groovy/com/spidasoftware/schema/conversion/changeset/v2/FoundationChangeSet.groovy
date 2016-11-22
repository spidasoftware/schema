package com.spidasoftware.schema.conversion.changeset.v2

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONObject

/**
 * Removes foundation and ground water level from pole json.
 */
class FoundationChangeSet extends ChangeSet {
	@Override
	String getSchemaPath() {
		"/schema/spidacalc/calc/project.schema"
	}

	@Override
	void apply(JSONObject json) throws ConversionException {
		//nothing needed as the new values are optional
	}

	@Override
	void revert(JSONObject json) throws ConversionException {
		json.get("leads")?.each { lead ->
			lead.get("locations")?.each { location ->
				location.get("designs")?.each { design ->
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
		}
	}
}
