/*
 * ©2009-2018 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v6

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import groovy.util.logging.Log4j

/**
 * In the schema v6 release we started exporting detailed results alongside summary results.  We also added the resultId
 * reference to detailed results.  When down converteing if we have detailed results in the design analysis we remove them,
 * then we still have the summary results because we always export summary results.
 * We also remove the analysis if it references detailed results as it isn't valid in previous versions.
 */
@Log4j
class RemoveDetailedResultsChangeset extends AbstractCalcDesignChangeset {

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        // Do nothing
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        if(designJSON.containsKey("analysis")) {
            List currentAnalysis = designJSON.get("analysis").collect()
            currentAnalysis.each { Map analysisObject ->
                boolean hasResults = analysisObject.containsKey("results") && analysisObject.get("results").size() > 0
                boolean isDetailedResults = hasResults && analysisObject.results.first().containsKey("analysisCase")
                boolean isResultId = analysisObject.containsKey("resultId")

                if(isDetailedResults || isResultId) {
                    designJSON.analysis.remove(analysisObject)
                }
            }
        }
    }
}
