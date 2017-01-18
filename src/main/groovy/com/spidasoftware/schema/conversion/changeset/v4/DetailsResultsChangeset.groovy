package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.FormatConverter
import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONArray
import net.sf.json.JSONObject

class DetailsResultsChangeset extends AbstractDesignChangeset {

    @Override
    void applyToDesign(JSONObject design) throws ConversionException {
        // Do nothing, can't go from summary result to detailed results
    }

    @Override
    void revertDesign(JSONObject design) throws ConversionException { // TODO
        if(design.containsKey("analysis")) {
            JSONArray currentAnalysis = design.getJSONArray("analysis")
            JSONArray summaryAnalysisResults = new JSONArray()
            currentAnalysis.each { JSONObject analysisObject ->
                JSONArray resultsObject = analysisObject.getJSONArray("results")
                if(resultsObject.size() > 0 && resultsObject.first().containsKey("component")) { // Already pre v4 analysis summary
                    summaryAnalysisResults.add(analysisObject)
                }  else {
                    JSONArray summaryAnalysis = new JSONArray()
                    resultsObject.each { JSONObject loadCaseResults ->
                        String loadCaseName = loadCaseResults.getString("analysisCase")
                        JSONArray resultsArray = new JSONArray()
                        loadCaseResults.getJSONArray("components").each { JSONObject componentResult ->
                            resultsArray.add(FormatConverter.convertDetailedResultToSummaryResults(componentResult))
                        }
                        summaryAnalysisResults.add(JSONObject.fromObject(id: loadCaseName, results: resultsArray))
                    }
                }
            }
            design.put("analysis", summaryAnalysisResults)
        }
    }
}
