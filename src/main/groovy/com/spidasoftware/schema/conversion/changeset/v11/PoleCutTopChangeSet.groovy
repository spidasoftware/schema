/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet

/**
 * In 2024.1, we added the functionality to cut part of the top of the pole off.
 * The amount of pole cut off is stored in the cutTop property
 *
 * When down converting, remove the cutTop property
 *
 * When up converting, add the cutTop property with a value of 0
 */
class PoleCutTopChangeSet extends AbstractClientDataChangeSet {
    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        return false
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        super.applyToDesign(designJSON)
        applyToStructure(designJSON.structure as Map)
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)
        revertStructure(designJSON.structure as Map)
    }

    @Override
    boolean applyToResults(Map resultsJSON) throws ConversionException {
        super.applyToResults(resultsJSON)
        return applyToStructure(resultsJSON.analyzedStructure as Map)
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        super.revertResults(resultsJSON)
        return revertStructure(resultsJSON.analyzedStructure as Map)
    }

    protected boolean revertStructure(Map structureJSON) {
        Map pole = structureJSON?.pole as Map
        if (pole != null) {
            if (pole.containsKey("cutTop")) {
                pole.remove("cutTop")
                return true
            }
        }
        return false
    }

    protected boolean applyToStructure(Map structureJSON) {
        Map pole = structureJSON?.pole as Map
        if (pole != null) {
            pole.cutTop = [value: 0, unit: "METRE"]
            return true
        }
        return false
    }
}
