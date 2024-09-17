/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

@CompileStatic
class WireMountedEquipmentChangeSet extends AbstractClientDataChangeSet {

	@Override
	boolean applyToClientData(Map clientDataJSON) throws ConversionException {
		// do nothing
		return false
	}

	@Override
	boolean revertClientData(Map clientDataJSON) throws ConversionException {
		boolean anyChanged = clientDataJSON.remove("wireMountedEquipments") != null

		clientDataJSON.assemblies?.each { Map assembly ->
			anyChanged |= revertStructure(assembly.assemblyStructure as Map)
		}
		return anyChanged
	}

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		// do nothing
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		super.revertDesign(designJSON)

		if (designJSON.structure != null) {
			revertStructure(designJSON.structure as Map)
		}
	}

	@Override
	boolean applyToResults(Map resultsJSON) {
		// do nothing
		return false
	}

	@Override
	boolean revertResults(Map resultsJSON) throws ConversionException {
		boolean anyChanged = super.revertResults(resultsJSON)

		if (resultsJSON.analyzedStructure != null) {
			anyChanged |= revertStructure(resultsJSON.analyzedStructure as Map)
		}

		return anyChanged
	}

	protected boolean revertStructure(Map structureJSON) {
		return structureJSON.remove("wireMountedEquipments") != null
	}
}
