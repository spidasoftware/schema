/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v3

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Log4j
import net.sf.json.JSONObject

@Log4j
class WEPEnvironmentChangeSet extends ChangeSet {
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
					structure?.get("wireEndPoints")?.each { wep ->
						if (wep.containsKey("environment")) {
							wep.remove("environment")
						}
					}
				}
			}
		}
	}
}

