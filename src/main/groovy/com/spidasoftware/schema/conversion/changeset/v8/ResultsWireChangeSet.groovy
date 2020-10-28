/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v8

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractResultsChangeSet

class ResultsWireChangeSet extends AbstractResultsChangeSet {

	@Override
	void applyToResults(Map resultsJSON) throws ConversionException {
		//DO NOTHING
	}

	@Override
	void revertResults(Map resultsJSON) throws ConversionException {
		resultsJSON?.results?.each { result ->
			result?.components?.each { component ->
				component?.wires?.each { wire ->
					if(wire.tensionMethod == "NONLINEAR_STRESS_STRAIN"){
						wire.tensionMethod = "DYNAMIC"
					}
				}
			}
		}
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