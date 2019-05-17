/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException


class PoleTemperatureChangeset extends AbstractCalcDesignChangeset {

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {

    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        Map pole = designJSON.get("structure")?.get("pole")
        Map temperature = pole?.get("temperature")
        if(temperature?.get("unit") == "CELSIUS") {
            double celsiusValue = temperature.get("value")
            double farenheitValue = celsiusValue * 1.8D + 32.0D

            temperature.put("value", farenheitValue)
            temperature.put("unit", "FAHRENHEIT")
        }
    }
}
