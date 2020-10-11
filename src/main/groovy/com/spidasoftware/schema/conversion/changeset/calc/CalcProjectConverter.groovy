/*
 * ©2009-2019 SPIDAWEB LLC
 */
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
    boolean applyChangeset(ChangeSet changeSet, Map json) {
        changeSet.applyToProject(json)
        return true // always return true for now because there is no use case to check if it has been converted/not converted
    }

    @Override
    boolean revertChangeset(ChangeSet changeSet, Map json) {
        changeSet.revertProject(json)
        return true // always return true for now because there is no use case to check if it has been converted/not converted
    }
}
