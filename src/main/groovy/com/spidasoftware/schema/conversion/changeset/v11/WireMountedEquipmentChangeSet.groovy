/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import groovy.transform.CompileStatic

@CompileStatic
class WireMountedEquipmentChangeSet extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		// do nothing
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		Map structure = designJSON.structure as Map
		structure?.remove("wireMountedEquipments")
	}
}
