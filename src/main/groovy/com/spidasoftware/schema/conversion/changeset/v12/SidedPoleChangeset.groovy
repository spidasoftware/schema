/*
 * Copyright (c) 2025 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * Throws a ConversionException if a non-round pole is present in the client data.  Otherwise does nothing.
 */
@CompileStatic
class SidedPoleChangeset extends AbstractClientDataChangeSet {


    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        // do nothing
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        // if non-round pole is present throw ConversionException
        clientDataJSON.poles?.each { Map pole ->
            if (pole.shape != "ROUND") {
                throw new ConversionException("Cannot downconvert a ${pole.shape} pole.")
            }
        }

        return false
    }
}
