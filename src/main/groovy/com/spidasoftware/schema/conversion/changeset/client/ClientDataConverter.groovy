package com.spidasoftware.schema.conversion.changeset.client

import com.spidasoftware.schema.conversion.changeset.AbstractConverter
import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.util.logging.Log4j

@Log4j
class ClientDataConverter extends AbstractConverter {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/client/data.schema"
    }

    @Override
    void updateVersion(Map json, int version) {
        //todo discuss how we want to do changesets
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

