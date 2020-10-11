/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset

interface Converter {

    String getSchemaPath()
    boolean convert(Map json, int toVersion) throws ConversionException
    int getCurrentVersion()
    void addChangeSet(int version, ChangeSet changeSet)
    void updateVersion(Map json, int version)
}
