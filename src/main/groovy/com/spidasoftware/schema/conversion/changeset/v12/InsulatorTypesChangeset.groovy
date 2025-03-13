package com.spidasoftware.schema.conversion.changeset.v12

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet

class InsulatorTypesChangeset extends AbstractClientDataChangeSet {

	@Override
	boolean applyToClientData(Map clientDataJSON) throws ConversionException {
		// do nothing
		return false
	}

	@Override
	boolean revertClientData(Map clientDataJSON) throws ConversionException {
		boolean anyChanged = false
		clientDataJSON.analysisCases?.each { Map analysisCase ->
			anyChanged |= removeInsulatorTypesFromLoadCase(analysisCase)
		}

		return anyChanged
	}

	@Override
	void revertProject(Map projectJSON) throws ConversionException {
		super.revertProject(projectJSON)

		projectJSON.defaultLoadCases?.each { Map loadCaseJSON ->
			removeInsulatorTypesFromLoadCase(loadCaseJSON)
		}
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		super.revertDesign(designJSON)

		designJSON.analysis?.each { Map analysisMap ->
			if (analysisMap.analysisCaseDetails != null) {
				removeInsulatorTypesFromLoadCase(analysisMap.analysisCaseDetails as Map)
			}
		}
	}

	@Override
	boolean revertResults(Map resultsJSON) throws ConversionException {
		boolean changed = super.revertResults(resultsJSON)
		resultsJSON?.results?.each { result ->
			result?.components?.each { component ->
				Map comp = component as Map
				if (comp.containsKey("uplift")) {
					comp.remove("uplift")
					changed = true
				}
			}
			Map details = result.analysisCaseDetails as Map
			if (details.containsKey("insulatorTypes")) {
				details.remove("insulatorTypes")
				changed = true
			}
		}
		return changed
	}

	boolean removeInsulatorTypesFromLoadCase(Map loadCaseJSON) {
		boolean anyChanged = false
		if (loadCaseJSON.containsKey("insulatorTypes")) {
			loadCaseJSON.remove("insulatorTypes")
			anyChanged = true
		}
		return anyChanged
	}
}
