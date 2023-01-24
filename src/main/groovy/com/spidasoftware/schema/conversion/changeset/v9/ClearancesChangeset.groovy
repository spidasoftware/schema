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
		if (clientDataJSON.remove("environments" ) != null) {
			anyChanged = true
		}
		if (clientDataJSON.remove("defaultEnvironment") != null) {
			anyChanged = true
		}
		if (clientDataJSON.remove("defaultWireClasses") != null) {
			anyChanged = true
		}
		if (clientDataJSON.remove("clearanceCases") != null) {
			anyChanged = true
		}
		if (clientDataJSON.remove("componentGroups") != null) {
			anyChanged = true
		}
		if (clientDataJSON.remove("wireClasses") != null) {
			anyChanged = true
		}
		if (clientDataJSON.remove("wireStates") != null) {
			anyChanged = true
		}
		return anyChanged
	}

	@Override
	void revertProject(Map projectJSON) throws ConversionException {
		super.revertProject(projectJSON)
		projectJSON.remove("defaultClearanceCases")
		projectJSON.remove("appliedTerrainLayers")
		if (projectJSON.containsKey("clientData")) {
			Map clientDataJSON = projectJSON.clientData as Map
			revertClientData(clientDataJSON)
		}
	}

	@Override
	void revertDesign(Map designJSON) {
		super.revertDesign(designJSON)
		revertStructure(designJSON.structure as Map)
		designJSON.remove("clearanceCases")
		designJSON.remove("clearanceResults")
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
            if(wep.remove("environmentRegions") != null) {
                anyChanged = true
            }
		}
		return anyChanged
	}
}
