/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.client

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcConverter
import groovy.util.logging.Log4j

@Log4j
/**
 * Converter for clientData json files.
 */
class ClientDataConverter extends AbstractCalcConverter {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/client/data.schema"
    }

    @Override
    void updateVersion(Map json, int version) {
        boolean versionAllowedInClientData = isVersionAllowedInClientData(version)
        if(versionAllowedInClientData) {
            json.put("version", version)
        }
    }

    @Override
    void applyChangeset(ChangeSet changeSet, Map json) {
        changeSet.applyToClientData(json)
    }

    @Override
    void revertChangeset(ChangeSet changeSet, Map json) {
        changeSet.revertClientData(json)
    }
}

