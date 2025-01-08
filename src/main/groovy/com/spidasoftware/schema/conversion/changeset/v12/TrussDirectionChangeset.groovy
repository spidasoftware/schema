/*
 * Copyright (c) 2025 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * When up-converting, update the trusses direction to be the compassDirection
 * When down-converting, revert the compassDirection back to direction
 */
@CompileStatic
class TrussDirectionChangeset extends AbstractClientDataChangeSet {

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        clientDataJSON.assemblies?.each { Map assembly ->
            revertStructure(assembly.assemblyStructure as Map)
        }
        return false
    }

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        clientDataJSON.assemblies?.each { Map assembly ->
            applyToStructure(assembly.assemblyStructure as Map)
        }
        return false
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        super.revertDesign(designJSON)
        revertStructure(designJSON.structure as Map)
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        super.applyToDesign(designJSON)
        applyToStructure(designJSON.structure as Map)
    }

    @Override
    boolean revertResults(Map resultsJSON) throws ConversionException {
        boolean anyChanged = super.revertResults(resultsJSON)
        revertStructure(resultsJSON.analyzedStructure as Map)
        return anyChanged
    }

    @Override
    boolean applyToResults(Map resultsJSON) throws ConversionException {
        boolean anyChanged = super.applyToResults(resultsJSON)
        applyToStructure(resultsJSON.analyzedStructure as Map)
        return anyChanged
    }

    private void applyToStructure(Map structureJSON) {
        structureJSON?.trusses?.each { Map truss ->
            double direction = truss.direction as double
            truss.direction = reverseDirection(direction)
        }
    }

    private void revertStructure(Map structureJSON) {
        structureJSON?.trusses?.each { Map truss ->
            double compassDirection = truss.direction as double
            truss.direction = reverseDirection(compassDirection)
        }
    }

    private double reverseDirection(double dir) {
        double reversedDir = 360d - dir
        // constrain value into 0-359 range
        reversedDir = reversedDir % 360
        if (reversedDir < 0) {
            reversedDir += 360
        }
        return reversedDir
    }
}
