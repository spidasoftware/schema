package com.spidasoftware.schema.conversion.changeset

interface Converter {

    String getSchemaPath()
    void convert(Map json, int toVersion) throws ConversionException
    int getCurrentVersion()
    void addChangeSet(int version, ChangeSet changeSet)
}
