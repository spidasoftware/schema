package com.spidasoftware.schema.conversion.changeset

class DesignConverter extends AbstractConverter {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/design.schema"
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

