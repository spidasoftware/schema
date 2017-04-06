package com.spidasoftware.schema.conversion.changeset


class ProjectConverter extends AbstractConverter {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/project.schema"
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
