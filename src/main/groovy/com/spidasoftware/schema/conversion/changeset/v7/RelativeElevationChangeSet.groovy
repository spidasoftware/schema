/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v7

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset

/**
 * v7.3 adds relative elevation to wireEndPoints. Removes "relative elevation" when we down convert.
 */
class RelativeElevationChangeSet extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		//do nothing
	}

	/**
	 * If there are detailed results, add them to the "analysis" section.  Otherwise, add resultId to the "analysis" section.
	 * Remove "analysisDetails" section.
	 */
	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		def wireEndPoints = designJSON.structure?.wireEndPoints
		wireEndPoints.each { Map wireEndPoint ->
			wireEndPoint.remove("relativeElevation")
		}
	}
}
