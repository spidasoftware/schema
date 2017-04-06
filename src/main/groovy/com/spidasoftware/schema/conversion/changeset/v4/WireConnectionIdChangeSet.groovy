/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Log4j


@Log4j
class WireConnectionIdChangeSet extends AbstractDesignChangeset {

	@Override
	void applyToDesign(Map json) throws ConversionException {
		//Do nothing
	}

	@Override
	void revertDesign(Map design) throws ConversionException {
		design.get("structure")?.get("wires")?.each { wire ->
			wire.remove("connectionId")
		}
	}
}
