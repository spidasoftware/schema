/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Log4j
import net.sf.json.JSONObject

@Log4j
class SpanGuyTypeChangeSet extends AbstractDesignChangeset {

	@Override
	void applyToDesign(JSONObject design) throws ConversionException {
		design.get("structure")?.get("spanGuys")?.each { spanGuy ->
        	spanGuy.put("type", "SUPPORT")
      	}
	}

	@Override
	void revertDesign(JSONObject design) throws ConversionException {
		design.get("structure")?.get("spanGuys")?.each { spanGuy ->
			if (spanGuy.containsKey("type")) {
			  spanGuy.remove("type")
			}
      	}
	}
}
