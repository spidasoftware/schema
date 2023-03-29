/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v3

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Slf4j


@Slf4j
class WEPEnvironmentChangeSet extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map json) throws ConversionException {
		// we don't need to change anything to the json -- this
	}

	@Override
	void revertDesign(Map design) throws ConversionException {
		def structure = design.get("structure")
		structure?.get("wireEndPoints")?.each { wep ->
			if (wep.containsKey("environment")) {
				wep.remove("environment")
			}
		}
	}
}

