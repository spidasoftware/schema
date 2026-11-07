/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v14

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

/**
 * Schema v14 adds an optional modelingType (MOMENT_CAPACITY | STRUCTURAL) to client trusses along with the
 * section, material and capacity-table properties a STRUCTURAL truss needs.
 *
 * Applying is a no-op: a v13 truss without modelingType is read as MOMENT_CAPACITY by the application.
 * Reverting strips every v14 property so the truss is left as a plain v13 MOMENT_CAPACITY truss. The required
 * momentCapacity is kept, so a STRUCTURAL truss is demoted rather than removed.
 */
@CompileStatic
class StructuralTrussChangeSet extends AbstractClientDataChangeSet {

    static final List<String> V14_TRUSS_PROPERTIES = [
            "modelingType",
            "width",
            "height",
            "topWidth",
            "topHeight",
            "momentOfInertia",
            "lateralMomentOfInertia",
            "topMomentOfInertia",
            "crossSectionalArea",
            "topCrossSectionalArea",
            "sectionModulus",
            "lateralSectionModulus",
            "modulus",
            "poissonRatio",
            "density",
            "weight",
            "materialCategory",
            "stressAllow",
            "embedmentDepth",
            "momentCapacityAtHeights",
            "lateralMomentCapacityAtHeights"
    ].asImmutable()

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        // do nothing - an absent modelingType is read as MOMENT_CAPACITY
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean changed = false
        (clientDataJSON.trusses as List<Map>)?.each { Map truss ->
            changed |= revertTruss(truss)
        }
        return changed
    }

    /**
     * @return true if any v14 property was removed from the truss
     */
    protected static boolean revertTruss(Map truss) {
        boolean changed = false
        V14_TRUSS_PROPERTIES.each { String property ->
            if (truss.containsKey(property)) {
                truss.remove(property)
                changed = true
            }
        }
        return changed
    }
}
