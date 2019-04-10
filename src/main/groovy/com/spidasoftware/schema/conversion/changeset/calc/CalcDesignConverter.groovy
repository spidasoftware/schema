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
        boolean versionAllowedInLocationAndDesign = isVersionAllowedInLocationAndDesign(version)
        if(versionAllowedInLocationAndDesign) {
            json.put("version", version)
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

