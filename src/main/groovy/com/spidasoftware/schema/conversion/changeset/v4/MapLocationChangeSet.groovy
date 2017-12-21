package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException


class MapLocationChangeSet extends AbstractCalcDesignChangeset {

    @Override
    void applyToDesign(Map json) throws ConversionException {
    }

    @Override
    void revertDesign(Map design) throws ConversionException {
        design.remove("mapLocation")
    }
}
