/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v11

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet

class ClientPoleSettingTypeChangeSet extends AbstractClientDataChangeSet {

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean anyChanged = false
        clientDataJSON.poles?.each { Map clientPole ->
            clientPole.remove("settingType")
            clientPole.remove("customSettingDepth")
            anyChanged = true
        }
        return anyChanged
    }
}
