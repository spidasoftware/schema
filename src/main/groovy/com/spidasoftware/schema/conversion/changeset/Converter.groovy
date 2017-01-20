package com.spidasoftware.schema.conversion.changeset

import net.sf.json.JSONObject

interface Converter {

    String getSchemaPath()
    public void convert(JSONObject json, int toVersion) throws ConversionException
    int getCurrentVersion()
}
