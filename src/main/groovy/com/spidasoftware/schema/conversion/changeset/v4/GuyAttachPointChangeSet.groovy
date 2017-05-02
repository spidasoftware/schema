package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException

import java.util.Map.Entry

/**
 * This version adds wire point loads and simplifies all point load items
 */
class GuyAttachPointChangeSet extends AbstractDesignChangeset {

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		//do nothing
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		designJSON.get("structure")?.remove("guyAttachPoints")
	}
}
