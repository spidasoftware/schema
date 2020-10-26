/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.calc

import com.spidasoftware.schema.conversion.changeset.*
import groovy.util.logging.Log4j

@Log4j
class CalcDesignConverter extends AbstractCalcConverter {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/design.schema"
    }

    @Override
    void updateVersion(Map json, int version) {
        if(isVersionAllowedInLocationAndDesign(version)) {
            json.put("version", version)
            if (isVersionAllowedInClientData(version) && json.containsKey("analysisDetails") && ((Map)json.analysisDetails).containsKey("detailedResults")) {
                Map detailedResultsJSON = ((Map) json.analysisDetails).detailedResults as Map
                if (detailedResultsJSON.containsKey("clientData")) {
                    ((Map)detailedResultsJSON.clientData).put("version", version)
                }
            }
        }
    }

    @Override
    void applyChangeset(ChangeSet changeSet, Map json) {
        changeSet.applyToDesign(json)
    }

    @Override
    void revertChangeset(ChangeSet changeSet, Map json) {
        changeSet.revertDesign(json)
    }
}

