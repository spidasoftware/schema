package com.spidasoftware.schema.conversion.changeset.v2

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Log4j
import net.sf.json.JSONObject

/**
 * Changeset to handle new Pole Lean fields in /v1/schema/spidacalc/calc/pole.schema . These are optional fields, so
 * they are not added on apply, but they must be removed on revert
 */
@Log4j
class PoleLeanChangeSet extends ChangeSet {

	/**
	 * Get the schema this changeset applies to
	 * @return
	 */
	@Override
	String getSchemaPath() {
		"/schema/spidacalc/calc/project.schema"
	}

	/**
	 * Apply the changes to the json object in place
	 * @param json
	 * @return
	 */
	@Override
	void apply(JSONObject json) throws ConversionException {
		// we don't need to change anything to the json -- this
	}

	/**
	 * Reverse the changes to the json object in place
	 * @param json
	 */
	@Override
	void revert(JSONObject json) throws ConversionException {
		json.get("leads")?.each { lead ->
			lead.get("locations")?.each { location ->
				location.get("designs")?.each { design ->
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
		}
	}
}
