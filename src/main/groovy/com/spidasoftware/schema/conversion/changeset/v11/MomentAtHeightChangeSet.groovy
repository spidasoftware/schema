/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

@CompileStatic
class MomentAtHeightChangeSet extends AbstractClientDataChangeSet {

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false
        clientDataJSON.poles?.each { Map clientPole ->
            if (clientPole.allowable != null) {
                String allowable = (clientPole.allowable as Map).unit == "PASCAL" ? "maximumAllowableStress" : "maximumGroundLineMoment"
                clientPole.put(allowable, clientPole.allowable)
                clientPole.remove("allowable")
                anyChanged = true
            }
        }
        return anyChanged
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false
        clientDataJSON.poles?.each { Map clientPole ->
            if (clientPole.maximumAllowableStress != null) {
                clientPole.allowable = clientPole.maximumAllowableStress
                clientPole.remove("maximumAllowableStress")
            } else {
                clientPole.allowable = clientPole.maximumGroundLineMoment
                clientPole.remove("maximumGroundLineMoment")
            }
            clientPole.remove("momentAtHeights")
            anyChanged = true
        }
        return anyChanged
    }

}
