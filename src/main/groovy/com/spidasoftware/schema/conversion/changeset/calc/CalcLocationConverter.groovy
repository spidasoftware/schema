/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.calc

import com.spidasoftware.schema.conversion.changeset.*
import groovy.util.logging.Slf4j

@Slf4j
class CalcLocationConverter extends AbstractCalcConverter {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/location.schema"
    }

    @Override
    void updateVersion(Map json, int version) {
        if(isVersionAllowedInLocationAndDesign(version)) {
            json.put("version", version)
            json.get("designs")?.each { Map designJSON ->
                new CalcDesignConverter().updateVersion(designJSON, version)
            }
        }
    }

    @Override
    void applyChangeset(ChangeSet changeSet, Map json) {
        changeSet.applyToLocation(json)
    }

    @Override
    void revertChangeset(ChangeSet changeSet, Map json) {
        changeSet.revertLocation(json)
    }
}
