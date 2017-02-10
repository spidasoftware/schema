package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONObject

class PoleTemperatureChangeset extends AbstractDesignChangeset {

    @Override
    void applyToDesign(JSONObject designJSON) throws ConversionException {

    }

    @Override
    void revertDesign(JSONObject designJSON) throws ConversionException { 
        JSONObject pole = designJSON.get("structure")?.get("pole")
        JSONObject temperature = pole?.get("temperature")
        if(temperature?.get("unit") == "CELSIUS") {
            double celsiusValue = temperature.getDouble("value")
            double farenheitValue = celsiusValue * 1.8D + 32.0D

            temperature.put("value", farenheitValue)
            temperature.put("unit", "FAHRENHEIT")
        }
    }
}
