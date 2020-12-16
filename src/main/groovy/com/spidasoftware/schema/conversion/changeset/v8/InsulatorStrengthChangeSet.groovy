/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v8

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic
/**
 * Multiple load options were introduced in 7.4 for insulators to optionally replace max rated strength.
 * If multiple load options are used, down convert insulators by setting the max rated strength to the lowest value of the three options.
 */
class InsulatorStrengthChangeSet extends AbstractClientDataChangeSet {

	@Override
	boolean applyToClientData(Map clientDataJSON) throws ConversionException {
		return false //noop
	}

	@Override
	boolean revertClientData(Map clientDataJSON) throws ConversionException {
		boolean converted = false
		clientDataJSON.insulators.each { Map map ->

			if (map.containsKey("maxCantileverStrength") && map.containsKey("maxCompressionStrength") && map.containsKey("maxTensionStrength")) {
				def lowestMultiDimensionalValue = [
						((map.maxCantileverStrength as Map).value as double),
						((map.maxCompressionStrength as Map).value as double),
						((map.maxTensionStrength as Map).value as double)].min()
				def unit = (map.maxCantileverStrength as Map).unit
				map.put("strength", [value: lowestMultiDimensionalValue, unit: unit])
				converted = true
			}

			if (map.containsKey("maxCantileverStrength")) {
				map.remove("maxCantileverStrength")
				converted = true
			}

			if (map.containsKey("maxCompressionStrength")) {
				map.remove("maxCompressionStrength")
				converted = true
			}

			if (map.containsKey("maxTensionStrength")) {
				map.remove("maxTensionStrength")
				converted = true
			}

			if (map.containsKey("lineOfActionOffset")) {
				map.remove("lineOfActionOffset")
				converted = true
			}
		}
		return converted
	}
}
