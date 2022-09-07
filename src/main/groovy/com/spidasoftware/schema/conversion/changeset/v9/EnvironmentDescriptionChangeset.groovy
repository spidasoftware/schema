/*
 * ©2009-2022 SPIDAWEB LLC
 */

package com.spidasoftware.schema.conversion.changeset.v9

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

@CompileStatic
/**
 * Environment descriptions are not required in version 8.0.
 *
 * When client data is up converted, do nothing.
 *
 * When client data is down converted, check if the environment has a description.
 * If it does have a description, then do nothing.
 * If it does not have a description, then set the description as "N/A".
 */
class EnvironmentDescriptionChangeset extends AbstractClientDataChangeSet{
    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false

        if (clientDataJSON.containsKey("environments")) {
            List<Map> environmentsList = clientDataJSON.environments as List<Map>
            environmentsList.findAll { it.description == null }
                    .each {
                            it.description = "N/A"
                            anyChanged = true
                    }
            }
        return anyChanged
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        if (projectJSON.containsKey("clientData")) {
            Map clientDataJSON = projectJSON.clientData as Map
            revertClientData(clientDataJSON)
        }
    }
}
