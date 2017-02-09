package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.FormatConverter
import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONArray
import net.sf.json.JSONObject

import java.text.Format

class DetailedResultsChangeset extends AbstractDesignChangeset {

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
                if(analysisObject.containsKey("results") && analysisObject.getJSONArray("results").size() > 0) {
                    JSONArray resultsObject = analysisObject.getJSONArray("results")
                    if (resultsObject.first().containsKey("component")) {
                        // Already pre v4 analysis summary
                        summaryAnalysisResults.add(analysisObject)
                    } else {
                        JSONArray summaryAnalysis = new JSONArray()
                        resultsObject.each { JSONObject loadCase ->
                            loadCase.get("components").each { JSONObject componentResult ->
                                summaryAnalysis.add(FormatConverter.convertDetailedResultToSummaryResults(loadCase, componentResult))
                            }
                            summaryAnalysisResults.add(JSONObject.fromObject(id: loadCase.analysisCase, results: summaryAnalysis))
                        }

                    }
                }
            }
            design.put("analysis", summaryAnalysisResults)
        }
    }
}
