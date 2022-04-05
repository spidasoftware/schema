/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.calc

import com.spidasoftware.schema.conversion.changeset.*
import groovy.util.logging.Slf4j

@Slf4j
class CalcProjectConverter extends AbstractCalcConverter {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/project.schema"
    }

    @Override
    void updateVersion(Map json, int version) {
        json.put("version", version)
        if(isVersionAllowedInLocationAndDesign(version)) {
            json.get("leads")?.each { Map leadJSON ->
                leadJSON.get("locations")?.each { Map locationJSON ->
                    new CalcLocationConverter().updateVersion(locationJSON, version)
                }
            }
            if (json.containsKey("clientData") && isVersionAllowedInClientData(version)) {
                ((Map)json.clientData).put("version", version)
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
