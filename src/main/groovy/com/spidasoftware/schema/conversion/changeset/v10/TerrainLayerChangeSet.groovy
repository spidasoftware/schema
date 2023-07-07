package com.spidasoftware.schema.conversion.changeset.v10

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import groovy.transform.CompileStatic

@CompileStatic
class TerrainLayerChangeSet extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
        // do nothing
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		designJSON.remove("terrainLayer")
	}
}
