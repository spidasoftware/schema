package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException


class MapLocationChangeSet extends AbstractDesignChangeset {

    @Override
    void applyToDesign(Map json) throws ConversionException {
    }

    @Override
    void revertDesign(Map design) throws ConversionException {
        design.remove("mapLocation")
    }
}
