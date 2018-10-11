/*
 * ©2009-2018 SPIDAWEB LLC
 */

package com.spidasoftware.schema.conversion.changeset.v6

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import groovy.transform.CompileStatic

@CompileStatic
class RemoveTensionResultsChangeset extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		//do nothing
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		if(designJSON.containsKey("analysis")) {
			def analysisList = designJSON.analysis
			analysisList.each { Map analysis ->
				if (analysis.containsKey("results")) {
					def results = analysis.results as List<Map>
					results.removeAll { Map result ->
						result.analysisType == "TENSION"
					}
				}
			}
		}
	}
}
