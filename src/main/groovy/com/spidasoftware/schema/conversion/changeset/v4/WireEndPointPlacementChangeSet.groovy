/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Log4j
import net.sf.json.JSONObject

@Log4j
class WireEndPointPlacementChangeSet extends AbstractDesignChangeset {

	@Override
	void applyToDesign(JSONObject json) throws ConversionException {
		//Do nothing
	}

	@Override
	void revertDesign(JSONObject design) throws ConversionException {
		design.get("structure")?.get("wires")?.each { wire ->
			wire.remove("wireEndPointPlacement")
		}
	}
}
