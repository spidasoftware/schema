/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v8

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractResultsChangeSet

class ResultsWireChangeSet extends AbstractResultsChangeSet {

	@Override
	boolean applyToResults(Map resultsJSON) throws ConversionException {
		return false //DO NOTHING
	}

	@Override
	boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean anyChanged = false
		resultsJSON?.results?.each { result ->
			result?.components?.each { component ->
				component?.wires?.each { wire ->
					if(wire.tensionMethod == "NONLINEAR_STRESS_STRAIN"){
						wire.tensionMethod = "DYNAMIC"
                        anyChanged = true
					}
				}
			}
		}
		return anyChanged
	}

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		//DO NOTHING
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		if (designJSON.containsKey("analysisDetails") && ((Map)designJSON.analysisDetails).containsKey("detailedResults")) {
			Map detailedResultsJSON = ((Map)designJSON.analysisDetails).detailedResults as Map
			revertResults(detailedResultsJSON)
		}
	}

}