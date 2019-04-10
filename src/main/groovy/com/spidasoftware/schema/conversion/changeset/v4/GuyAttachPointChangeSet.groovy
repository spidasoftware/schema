/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException

import java.util.Map.Entry

/**
 * This version adds wire point loads and simplifies all point load items
 */
class GuyAttachPointChangeSet extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		//do nothing
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		designJSON.get("structure")?.remove("guyAttachPoints")
	}
}
