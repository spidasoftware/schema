/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * In Calc v25.0 we added the ability to override a load cases tension type during analysis and wire tension load calculations.
 *
 * When down-converting, remove tension override from assembly structures, design structures, and results analyzed structures
 * When up-converting, do nothing
 */
@CompileStatic
class TensionOverrideChangeset extends AbstractClientDataChangeSet {

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean changed = false
        clientDataJSON.assemblies?.each { Map assembly ->
            changed |= revertStructure(assembly.assemblyStructure as Map)
        }
        return changed
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)
        revertStructure(designJSON.structure as Map)
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean changed = super.revertResults(resultsJSON)
        changed |= revertStructure(resultsJSON.analyzedStructure as Map)
        return changed
    }

    protected boolean revertStructure(Map structureJSON) {
        boolean changed = false
        structureJSON?.wires?.each { Map wire ->
            changed |= (wire.remove("tensionOverride") != null)
        }
        return changed
    }
}
