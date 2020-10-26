/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.client

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

@Log4j
@CompileStatic

/**
 * Changesets that convert client data json objects. Can be applied independently to clientDatas or projectComponents.
 */
/**
 * Convert a client data json object between versions. Should be forwards and backwards compatible.
 */
abstract class AbstractClientDataChangeSet extends AbstractCalcDesignChangeset {

	abstract boolean applyToClientData(Map clientDataJSON) throws ConversionException
	abstract boolean revertClientData(Map clientDataJSON) throws ConversionException

	@Override
	void applyToProject(Map projectJSON) throws ConversionException {
		super.applyToProject(projectJSON)
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
			Map detailedResultsJSON = (designJSON.analysisDetails as Map).detailedResults as Map
			if (detailedResultsJSON.containsKey("clientData")) {
				Map clientDataJSON = detailedResultsJSON.clientData as Map
				if (applyToClientData(clientDataJSON) && clientDataJSON.containsKey("hash")) {
					clientDataJSON.remove("hash")
				}
			}
		}
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		if (designJSON.containsKey("analysisDetails") && ((Map)designJSON.analysisDetails).containsKey("detailedResults")) {
			Map detailedResultsJSON = (designJSON.analysisDetails as Map).detailedResults as Map
			if (detailedResultsJSON.containsKey("clientData")) {
				Map clientDataJSON = detailedResultsJSON.clientData as Map
				if (revertClientData(clientDataJSON) && clientDataJSON.containsKey("hash")) {
					clientDataJSON.remove("hash")
				}
			}
		}
	}
}
