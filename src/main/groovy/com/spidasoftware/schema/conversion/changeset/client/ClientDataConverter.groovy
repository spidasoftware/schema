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
        if (isVersionAllowedInClientData(version)) {
            json.put("version", version)
        }
    }

    @Override
    boolean applyChangeset(ChangeSet changeSet, Map json) {
        return changeSet.applyToClientData(json)
    }

    @Override
    boolean revertChangeset(ChangeSet changeSet, Map json) {
        return changeSet.revertClientData(json)
    }
}

