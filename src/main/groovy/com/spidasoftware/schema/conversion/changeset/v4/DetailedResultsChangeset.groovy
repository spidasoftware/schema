/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.FormatConverter
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException

class DetailedResultsChangeset extends AbstractCalcDesignChangeset {

    @Override
    void applyToDesign(Map design) throws ConversionException {
        // Do nothing, can't go from summary result to detailed results
    }

    @Override
    void revertDesign(Map design) throws ConversionException { // TODO
        if(design.containsKey("analysis")) {
            List currentAnalysis = design.get("analysis")
            List summaryAnalysisResults = []
            currentAnalysis.each { Map analysisObject ->
                if(analysisObject.containsKey("results") && analysisObject.get("results").size() > 0) {
                    List resultsObject = analysisObject.get("results")
                    if (resultsObject.first().containsKey("component")) {
                        // Already pre v4 analysis summary
                        summaryAnalysisResults.add(analysisObject)
                    } else {
                        resultsObject.each { Map loadCase ->
                            List summaryAnalysis = []
                            loadCase.get("components").each { Map componentResult ->
                                summaryAnalysis.add(FormatConverter.convertDetailedResultToSummaryResults(loadCase, componentResult))
                            }
                            summaryAnalysisResults.add(CalcProjectChangeSet.duplicateAsJson(id: loadCase.analysisCase, results: summaryAnalysis))
                        }

                    }
                }
            }
            design.put("analysis", summaryAnalysisResults)
        }
    }
}
