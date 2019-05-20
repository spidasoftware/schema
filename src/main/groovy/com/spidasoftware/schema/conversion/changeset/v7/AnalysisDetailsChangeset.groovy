/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v7

import com.spidasoftware.schema.conversion.FormatConverter
import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import groovy.transform.CompileStatic

/**
 * v7 adds analysisCurrent, moves detailed results from "analysis" to "analysisDetails", and adds the analyzed structure to analysis details.
 */
class AnalysisDetailsChangeset extends AbstractCalcDesignChangeset {

	/**
	 * Removes detailed analysis from "analysis".
	 * If detailed analysis is not external, adds "analysisCaseDetails" to each summary result.
	 * Creates new "analysisDetails" section, containing resultId and, if they are not external, "detailedResults".
	 */
	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		List<Map> analysis = designJSON.get("analysis") as List<Map>
		if(analysis == null) {
			return
		}

		// there will probably be only one object in this detailedResults list. Either a full detailed results object
		// or a "resultId" referencing an object in another place. But because the schema technically allows both,
		// we will make sure we correctly handle if both exist (we'll use the first one).
		List detailedResults = analysis.findAll { Map analysisObject ->
			List results = (List) analysisObject.get("results") ?: []
			boolean isDetailedResult = results?.size() > 0 && ((Map) results[0]).containsKey("analysisCase")
			return analysisObject.containsKey("resultId") || isDetailedResult
		}

		List<Map> summaryResults = analysis.findAll { !detailedResults.contains(it) }
		boolean hasDetailedResults = (detailedResults.size() > 0)
		if(hasDetailedResults) {
			// prior to this version, if analysis was not current then detailed results were not exported.
			designJSON.put("analysisCurrent", true)

			// remove detailed results from "analysis" section
			analysis.removeAll(detailedResults)

			// add analysis case details to summary results
			Map detailedResult = detailedResults[0]
			List<Map> results = detailedResult.get("results") as List<Map>
			if(results != null) {
				results.each { Map analysisCaseResult ->
					Map summaryResult = summaryResults.find { it.id == analysisCaseResult.analysisCase }
					if (summaryResult == null) {
						summaryResult = createSummaryResult(analysisCaseResult)
						analysis.add(summaryResult)
					}
					Map analysisCaseDetails = analysisCaseResult.get("analysisCaseDetails") as Map
					if (analysisCaseDetails != null) {
						summaryResult.put("analysisCaseDetails", analysisCaseDetails)
					}
				}
			}

			// create "analysisDetails" section
			Map analysisDetails = [:]
			String resultId = detailedResult.get("resultId")
			if(resultId != null) {
				resultId = resultId.replace(".json", "")
				detailedResult.remove("resultId")
				detailedResult.put("id", resultId)
			} else {
				resultId = detailedResult.get("id")
			}
			if(results) {
				analysisDetails.put("detailedResults", detailedResult)
			}
			analysisDetails.put("resultId", resultId)
			designJSON.put("analysisDetails", analysisDetails)
		} else {
			designJSON.put("analysisCurrent", false)
		}
	}

	/**
	 * If there are detailed results, add them to the "analysis" section.  Otherwise, add resultId to the "analysis" section.
	 * Remove "analysisDetails" section.
	 */
	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		if(!designJSON.containsKey("analysisDetails")) {
			return
		}

		List<Map> analysis = designJSON.get("analysis") as List<Map>
		if(analysis == null) {
			// shouldn't happen, but we'll play it safe
			analysis = []
			designJSON.put("analysis", analysis)
		}
		Map analysisDetails = designJSON.remove("analysisDetails") as Map
		if(analysisDetails.containsKey("detailedResults")) {
			analysis.add(analysisDetails.get("detailedResults") as Map)
		} else {
			String resultId = analysisDetails.get("resultId")
			analysis.add(["resultId": resultId + ".json"])
		}
	}

	/**
	 * Create a summary result from a detailed result.
	 */
	Map createSummaryResult(Map detailedResult) {
		Map summaryResult = ["id": detailedResult.analysisCase]
		List<Map> results = []
		detailedResult.components.each { Map componentResult ->
			results.add(FormatConverter.convertDetailedResultToSummaryResults(detailedResult, componentResult))
		}
		summaryResult.put("results", results)
		return summaryResult
	}
}
