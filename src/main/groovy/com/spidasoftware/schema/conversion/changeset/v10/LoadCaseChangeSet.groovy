/*
 * ©2009-2023 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v10

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * New load cases were added after Calc v8.0
 *
 * When down converting from schema v10, we want to remove these load cases
 */
@CompileStatic
class LoadCaseChangeSet extends AbstractClientDataChangeSet {

    // list of load cases we want to remove
    private List<String> loadCases = ["CSA 2020 Maximum Wind", "NESC Extreme Wind 2023", "NESC Extreme Ice 2023", "NESC 2023", "GO95 01-2020"]

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        if (clientDataJSON.containsKey("analysisCases")) {
            List<Map> analysisCases = clientDataJSON.analysisCases as List<Map>
            anyChanged = analysisCases.removeAll { loadCases.contains(it.type) }
        }

        return anyChanged
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)

        if (projectJSON.containsKey("defaultLoadCases")) {
            List<Map> defaultLoadCases = projectJSON.defaultLoadCases as List<Map>
            defaultLoadCases.removeAll { loadCases.contains(it.type) }
        }
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)

        if (designJSON.containsKey("analysis")) {
            List<Map> analysisList = designJSON.analysis as List<Map>
            analysisList.removeAll { loadCases.contains((it.analysisCaseDetails as Map)?.type) }
            // if load cases do not contain any summary results, then remove the analysisDetails as well
            // because sometimes analysisDetails has a resultId that references a results.json file that we cannot see
            if (!analysisList.any { it.hasProperty("results") && !(it.results as List<Map>).isEmpty() }) {
                designJSON.remove("analysisDetails")
                designJSON.analysisCurrent = false
            }
        }
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean anyChanged = super.revertResults(resultsJSON)

        if (resultsJSON.containsKey("results")) {
            List<Map> resultsList = resultsJSON.results as List<Map>
            anyChanged |= resultsList.removeAll { loadCases.contains((it.analysisCaseDetails as Map)?.type) }
        }
        return anyChanged
    }
}
