/*
 * Copyright (c) 2025 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

@CompileStatic
class ComponentBraceChangeset extends AbstractClientDataChangeSet {

	@Override
	boolean applyToClientData(Map clientDataJSON) throws ConversionException {
		// do nothing
		return false
	}

	@Override
	boolean revertClientData(Map clientDataJSON) throws ConversionException {
		boolean anyChanged = false
		if(clientDataJSON.containsKey("componentBraces")) {
			anyChanged = !(clientDataJSON.componentBraces as List).isEmpty()
			clientDataJSON.remove("componentBraces")
		}

		clientDataJSON.assemblies?.each { Map assembly ->
			anyChanged |= revertStructure(assembly.assemblyStructure as Map)
		}
		return anyChanged
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		super.revertDesign(designJSON)

		if (designJSON.structure != null) {
			revertStructure(designJSON.structure as Map)
		}
	}

	@Override
	boolean revertResults(Map resultsJSON) throws ConversionException {
		super.revertResults(resultsJSON)

		if (resultsJSON.analyzedStructure != null) {
			return revertStructure(resultsJSON.analyzedStructure as Map)
		}
		return false
	}

	protected boolean revertStructure(Map structureJSON) {
		boolean anyChanged = (structureJSON.componentBraces as List)?.size() > 0

		structureJSON.remove("componentBraces")
		(structureJSON.crossArms as List<Map>).each {
			it.remove("braces")
		}

		return anyChanged
	}
}
