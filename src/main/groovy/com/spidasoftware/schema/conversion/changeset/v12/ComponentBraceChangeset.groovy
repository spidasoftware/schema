/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
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

		clientDataJSON.analysisCases?.each { Map analysisCase ->
			anyChanged |= revertLoadCase(analysisCase)
		}

		return anyChanged
	}

	@Override
	void revertProject(Map projectJSON) {
		super.revertProject(projectJSON)
		projectJSON.defaultLoadCases?.each { revertLoadCase(it as Map) }
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		super.revertDesign(designJSON)

		if (designJSON.structure != null) {
			revertStructure(designJSON.structure as Map)
		}
		designJSON.analysis?.each { Map analysis ->
			revertLoadCase(analysis.analysisCaseDetails as Map)
		}
	}

	@Override
	boolean revertResults(Map resultsJSON) throws ConversionException {
		boolean changed = super.revertResults(resultsJSON)

		if (resultsJSON.analyzedStructure != null) {
			changed |= revertStructure(resultsJSON.analyzedStructure as Map)
		}
		resultsJSON.results?.each { Map result ->
			changed |= revertLoadCase(result.analysisCaseDetails as Map)

			List<Map> resultComponents = result.components as List<Map>
			resultComponents.each { Map component ->
				if (component.containsKey("componentBraces")) {
					List<Map> componentBraces = component.componentBraces as List<Map>
					if (!componentBraces.empty) {
						changed = true
					}
					component.remove("componentBraces")
				}
			}
		}

		return changed
	}

	protected boolean revertStructure(Map structureJSON) {
		boolean anyChanged = (structureJSON.componentBraces as List)?.size() > 0

		structureJSON.remove("componentBraces")
		(structureJSON.crossArms as List<Map>).each {
			it.remove("braces")
		}

		return anyChanged
	}

	protected boolean revertLoadCase(Map loadCaseJSON) {
		List<String> components = loadCaseJSON.components as List<String>
		if (components != null) {
			return components.remove("COMPONENT_BRACE")
		} else {
			return false
		}
	}
}
