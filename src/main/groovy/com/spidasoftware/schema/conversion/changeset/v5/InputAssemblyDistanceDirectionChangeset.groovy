/*
 * ©2009-2017 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v5

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Slf4j

@Slf4j
class InputAssemblyDistanceDirectionChangeset extends AbstractCalcDesignChangeset {
	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		// do nothing
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		def assemblies = designJSON.structure?.assemblies
		assemblies?.each {assembly ->
			assembly.remove("direction")
			assembly?.support?.each {support ->
				support.remove("distance")
			}
		}
	}
}
