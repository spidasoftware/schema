/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Slf4j

@Slf4j
class WireEndPointPlacementChangeSet extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map json) throws ConversionException {
		//Do nothing
	}

	@Override
	void revertDesign(Map design) throws ConversionException {
		design.get("structure")?.get("wires")?.each { wire ->
			wire.remove("wireEndPointPlacement")
		}
	}
}
