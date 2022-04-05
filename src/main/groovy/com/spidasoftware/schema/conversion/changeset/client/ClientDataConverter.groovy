/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.client

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcConverter
import groovy.util.logging.Slf4j

@Slf4j
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
        if (isVersionAllowedInClientData(version)) {
            json.put("version", version)
        }
    }

    @Override
    void applyChangeset(ChangeSet changeSet, Map json) {
        if (changeSet.applyToClientData(json) && json.containsKey("hash")) {
            json.remove("hash")
        }
    }

    @Override
    void revertChangeset(ChangeSet changeSet, Map json) {
        if (changeSet.revertClientData(json) && json.containsKey("hash")) {
            json.remove("hash")
        }
    }
}

