package com.spidasoftware.schema.conversion.changeset.calc

import com.spidasoftware.schema.conversion.changeset.*

class CalcLocationConverter extends AbstractConverter {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/location.schema"
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
