/*
 * ©2009-2017 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v5

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Log4j

@Log4j
class InputAssemblyDistanceDirectionChangeset extends AbstractDesignChangeset{
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
