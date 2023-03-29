/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.client

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractResultsChangeSet
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

@Log4j
@CompileStatic

/**
 * Convert a client data json object between versions. Should be forwards and backwards compatible.
 * Can be applied independently to a clientData or any projectComponents.
 */
abstract class AbstractClientDataChangeSet extends AbstractResultsChangeSet {

	abstract boolean applyToClientData(Map clientDataJSON) throws ConversionException
	abstract boolean revertClientData(Map clientDataJSON) throws ConversionException

	@Override
	void applyToProject(Map projectJSON) throws ConversionException {
		super.applyToProject(projectJSON)
		projectJSON.remove("clientFileVersion")
		if (projectJSON.containsKey("clientData")) {
			Map clientDataJSON = projectJSON.clientData as Map
			if (applyToClientData(clientDataJSON) && clientDataJSON.containsKey("hash")) {
				clientDataJSON.remove("hash")
			}
		}
	}

	@Override
	void revertProject(Map projectJSON) throws ConversionException {
		super.revertProject(projectJSON)
		projectJSON.remove("clientFileVersion")
		if (projectJSON.containsKey("clientData")) {
			Map clientDataJSON = projectJSON.clientData as Map
			if (revertClientData(clientDataJSON) && clientDataJSON.containsKey("hash")) {
				clientDataJSON.remove("hash")
			}
		}
	}

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		if (designJSON.containsKey("analysisDetails") && ((Map)designJSON.analysisDetails).containsKey("detailedResults")) {
			Map detailedResultsJSON = ((Map)designJSON.analysisDetails).detailedResults as Map
			applyToResults(detailedResultsJSON)
		}
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		if (designJSON.containsKey("analysisDetails") && ((Map)designJSON.analysisDetails).containsKey("detailedResults")) {
			Map detailedResultsJSON = ((Map)designJSON.analysisDetails).detailedResults as Map
			boolean anyChanged = revertResults(detailedResultsJSON)
			if (anyChanged) {
				designJSON.analysisCurrent = false
			}
		}
	}

	@Override
	boolean applyToResults(Map resultsJSON) throws ConversionException {
        boolean anyChanged = false
		if (resultsJSON.containsKey("clientData")) {
			Map clientDataJSON = resultsJSON.clientData as Map
			if (applyToClientData(clientDataJSON) && clientDataJSON.containsKey("hash")) {
				clientDataJSON.remove("hash")
                anyChanged = true
			}
		}
        return anyChanged
	}

	@Override
	boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean anyChanged = false
		if (resultsJSON.containsKey("clientData")) {
			Map clientDataJSON = resultsJSON.clientData as Map
			if (revertClientData(clientDataJSON) && clientDataJSON.containsKey("hash")) {
				clientDataJSON.remove("hash")
                anyChanged = true
			}
		}
        return anyChanged
	}
}
