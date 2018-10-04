/*
 * ©2009-2018 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v6

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import groovy.util.logging.Log4j

/**
 * In the schema v6 release we added Bundle type wires. On downconversion, we need to
 * replace bundles with client wire with COMMUNICATION usage group, same tension group, same size.
 * The user would then have opportunity to map this likely non-existent wire to a real wire from their
 * client data.
 */
@Log4j
class RevertBundleChangeset extends AbstractCalcDesignChangeset {

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        // Do nothing
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        def bundleProps = ["group",
                           "bundleComponents",
                           "messenger",
                           "autoCalculateDiameter",
                           "aliases",
                           "diameter",
                           "source"]

        designJSON.structure?.wires?.each { Map wire ->
            if (wire.usageGroup == "COMMUNICATION_BUNDLE") {
                wire.usageGroup = "COMMUNICATION"
                wire.clientItem.coreStrands = 1
                wire.clientItem.conductorStrands = 0
                wire.remove("clientItemVersion")

                bundleProps.each {
                    wire.clientItem.remove(it)
                }
            }
        }
    }
}
