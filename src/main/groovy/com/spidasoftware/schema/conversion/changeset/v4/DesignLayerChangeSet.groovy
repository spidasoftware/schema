/*
 * ©2009-2017 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j


/**
 * Removes the layerType key added in 7.
 */
@Log4j
@CompileStatic
class DesignLayerChangeSet extends AbstractCalcDesignChangeset{

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		// do nothing - nothing we need to add.
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		designJSON.remove("layerType")
	}
}
