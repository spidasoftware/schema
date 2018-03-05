package com.spidasoftware.schema.conversion.changeset.calc

import com.spidasoftware.schema.conversion.changeset.*
import groovy.util.logging.Log4j

@Log4j
class CalcLocationConverter extends AbstractConverter {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/location.schema"
    }

    @Override
    void updateVersion(Map json, int version) {
        boolean versionAllowedInLocationAndDesign = isVersionAllowedInLocationAndDesign(version)
        if(versionAllowedInLocationAndDesign) {
            json.put("version", version)

            json.get("designs")?.each { Map designJSON ->
                designJSON.put("version", version)
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
