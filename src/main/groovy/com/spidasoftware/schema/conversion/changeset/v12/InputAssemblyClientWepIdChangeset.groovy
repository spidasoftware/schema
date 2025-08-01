/*
 * Copyright (c) 2025 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset

class InputAssemblyClientWepIdChangeset extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		// do nothing
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		def assemblies = designJSON.structure?.assemblies
		assemblies?.each { assembly ->
			assembly.wireEndPoints?.each { wep ->
				wep.remove("clientDataWEPid")
			}
		}
	}

}
