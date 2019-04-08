package com.spidasoftware.schema.conversion.changeset.calc

import com.spidasoftware.schema.conversion.changeset.*
import groovy.util.logging.Log4j

@Log4j
class CalcProjectConverter extends AbstractCalcConverter {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/project.schema"
    }

    @Override
    void updateVersion(Map json, int version) {
        json.put("version", version)
        boolean versionAllowedInLocationAndDesign = isVersionAllowedInLocationAndDesign(version)
        if(versionAllowedInLocationAndDesign) {
            json.get("leads")?.each { Map leadJSON ->
                leadJSON.get("locations")?.each { Map locationJSON ->
                    locationJSON.put("version", version)

                    locationJSON.get("designs")?.each { Map designJSON ->
                        designJSON.put("version", version)
                    }
                }
            }
        }
    }

    @Override
    void applyChangeset(ChangeSet changeSet, Map json) {
        changeSet.applyToProject(json)
    }

    @Override
    void revertChangeset(ChangeSet changeSet, Map json) {
        changeSet.revertProject(json)
    }
}
