/*
 * ©2009-2022 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v9

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * Remove the previously-disallowed elements that were added for clearances.
 */
@CompileStatic
class ClearancesChangeset extends AbstractClientDataChangeSet {

	@Override
	boolean applyToClientData(Map clientDataJSON) throws ConversionException {
		return false
	}

	@Override
	boolean revertClientData(Map clientDataJSON) throws ConversionException {
		boolean anyChanged = false
		(clientDataJSON.assemblies as List<Map>).each {
			anyChanged |= revertStructure(it.assemblyStructure as Map)
		}
		return anyChanged
	}

	@Override
	void revertDesign(Map designJSON) {
		super.revertDesign(designJSON)
		revertStructure(designJSON.structure as Map)
	}

	@Override
	boolean revertResults(Map resultsJSON) {
		boolean anyChanged = super.revertResults(resultsJSON)
		anyChanged |= revertStructure(resultsJSON.analyzedStructure as Map)
		return anyChanged
	}

	protected boolean revertStructure(Map structureJSON) {
		boolean anyChanged = false
		structureJSON?.wireEndPoints?.each { Map wep ->
			if(wep.remove("calculateClearances") != null) {
				anyChanged = true
			}
		}
		return anyChanged
	}
}
