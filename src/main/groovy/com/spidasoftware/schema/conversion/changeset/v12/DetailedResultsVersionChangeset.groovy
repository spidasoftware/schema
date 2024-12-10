/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * When up-converting, add the version property with value of 12
 * When down-converting, remove version property
 *
 * When up or down converting, adding or removing the version property should not return true
 */
@CompileStatic
class DetailedResultsVersionChangeset extends AbstractClientDataChangeSet {
    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        return false
    }

    @Override
    boolean applyToResults(Map resultsJSON) throws ConversionException {
        boolean changed = super.applyToResults(resultsJSON)
        resultsJSON.version = 12
        return changed
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean changed = super.revertResults(resultsJSON)
        resultsJSON.remove("version")
        return changed
    }
}
