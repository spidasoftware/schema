/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Slf4j


@Slf4j
class SpanGuyTypeChangeSet extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map design) throws ConversionException {
		design.get("structure")?.get("spanGuys")?.each { spanGuy ->
        	spanGuy.put("type", "SUPPORT")
      	}
	}

	@Override
	void revertDesign(Map design) throws ConversionException {
		design.get("structure")?.get("spanGuys")?.each { spanGuy ->
			if (spanGuy.containsKey("type")) {
			  spanGuy.remove("type")
			}
			if (spanGuy.containsKey("loads")) {
				spanGuy.remove("loads")
			}
      	}
	}
}
