/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v7

import com.spidasoftware.schema.conversion.FormatConverter
import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import groovy.transform.CompileStatic

// todo comment, test
@CompileStatic
class AnalysisDetailsChangeset extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		/*
		find detailed analysis in analysis
		if found
			remove from analysis
			find all analysis cases
			for each analysis case
				find the summary result for that analysis case
				add as analysisCaseDetails to the summary result
			add as "resultId" and "detailedResults" in "analysisDetails"
		 */
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
			designJSON.put("analysisCurrent", true)

			analysis.removeAll(detailedResults)

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

			Map analysisDetails = [:]
			String resultId = detailedResult.get("resultId")
			if(resultId != null) {
				resultId = resultId.replace(".json", "")
				detailedResult.remove("resultId")
				detailedResult.put("id", resultId)
			} else {
				resultId = detailedResult.get("id")  // todo need to remove .json?
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

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		/*
		If analysisDetails exists
			If detailedResults exists
				add detailedResults to analysis  todo ok to have summary and detail for same analysis cases?
			else
				add id as resultId (+.json) to analysis
			remove analysisDetails.

		Leave analysis*.analysisCaseDetails in place.
		 */
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

	/*
	Old version:

	if analysis current:
		"analysis" list of, for analyzed structure:
			- Detailed results (only if analysis is current)
				Result Details, with analysis case details embedded (insertAnalysisCaseDetails())  ("../../spidacalc/results/results.schema")
				OR detailed results id  ([resultId: "${resultDetails.id}.json".toString()])

			- summary results (current or not, if they exist)
				list of ["id" : analysis case name,
						 "results" : analysisCaseResult (if they exist)]  ("../../spidamin/asset/standard_details/analysis_asset.schema")

	if analysis not current:
		"analysis" list of, for editing structure:
			"id" : analysis case name

	"analysis" may not exist at all (analysis not current, no load or strength cases on editing structure)
	 */

	/*
	New version:
		"analysis" list for each analysis case on the editing structure:
			"id" : analysis case name
			"analysisCaseDetails" : analysis case (may not exist)
			"results" : analysis case result (only written if analysis is current - but may be present on previous-version designs with not-current results)

		"analysisDetails" if detailed results exist (current or not) and if exporting detailed results or detailed result ids
			"resultId" : detailed result id, implied ".json" to find external results if detailedResults is not present
			"detailedResults" : detailed results, with analysis case details embedded if exporting detailed results
			"analyzedStructure" : analyzed structure if exporting detailed results, else may be in external results file or may not exist
	 */

	/*
	resultDetails?.results?."analysisCaseDetails"
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
