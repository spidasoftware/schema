/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Log4j


@Log4j
class ConnectivityChangeSet extends AbstractDesignChangeset {

	@Override
	void applyToDesign(Map json) throws ConversionException {
		//Do nothing
	}

	@Override
	void revertDesign(Map design) throws ConversionException {
		def structure = design.structure
		if (structure) {
			structure.wires?.each { wire ->
				wire.remove("connectionId")
				wire.remove("connectedWire")
			}
			structure.wireEndPoints?.each { wireEndPoint ->
				wireEndPoint.remove("connectionId")
			}
			structure.spanPoints?.each {spanPoint ->
				spanPoint.remove("connectionId")
			}
			// wire point loads are also connected, but they are removed entirely
			structure.spanGuys?.each {spanGuy ->
				spanGuy.remove("connectionId")
			}
		}

	}
}
