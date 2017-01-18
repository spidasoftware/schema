/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v3

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Log4j
import net.sf.json.JSONObject

@Log4j
class WEPEnvironmentChangeSet extends AbstractDesignChangeset {

	@Override
	void applyToDesign(JSONObject json) throws ConversionException {
		// we don't need to change anything to the json -- this
	}

	@Override
	void revertDesign(JSONObject design) throws ConversionException {
		def structure = design.get("structure")
		structure?.get("wireEndPoints")?.each { wep ->
			if (wep.containsKey("environment")) {
				wep.remove("environment")
			}
		}
	}
}

