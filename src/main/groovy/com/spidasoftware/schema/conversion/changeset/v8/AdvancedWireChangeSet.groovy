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
 * Advanced wires were introduced in 7.3.
 * Down converts all advanced wires to basic when exporting exchange file to a version older than 7.3.
 */
class AdvancedWireChangeSet extends AbstractClientDataChangeSet {

	private final static double POUND_FORCE_PER_SQUARE_INCH_TO_PASCAL = 1.450377377302092E-4d
	private final static double PER_FAHRENHEIT_TO_PER_CELSIUS = 0.5555555555555556d

	@Override
	boolean applyToClientData(Map clientDataJSON) throws ConversionException {
		return false //noop: there are no up conversions to 7.3
	}

	@Override
	boolean revertClientData(Map clientDataJSON) throws ConversionException {
		boolean converted = false
		clientDataJSON.wires.each { Map clientWireJSON ->
			if (clientWireJSON.calculation == "NONLINEAR_STRESS_STRAIN") {
				if (revertAdvancedClientWire(clientWireJSON)) {
					converted = true
				}
			}
		}
		return converted
	}

	/**
	 * Change calculation type from non-linear to dynamic
	 * Calculate the expansion coeff and modulus
	 * Throw away all the polynomial data
	 */
	@CompileDynamic
	boolean revertAdvancedClientWire(Map clientWireJSON) {
		clientWireJSON.calculation = "DYNAMIC"
		clientWireJSON.put("modulus", [value: calculateModulus(clientWireJSON), unit: "PASCAL"])
		clientWireJSON.put("expansionCoefficient", [value: calculatedExpansionCoefficient(clientWireJSON), unit: "PER_CELSIUS"])
		clientWireJSON.conductorProperties = "TOTAL"
		clientWireJSON.remove("outerModulus")
		clientWireJSON.remove("coreModulus")
		clientWireJSON.remove("outerExpansionCoefficient")
		clientWireJSON.remove("coreExpansionCoefficient")
		clientWireJSON.remove("stressStrainPolynomials")
		clientWireJSON.remove("creepPolynomials")
	}

	/*** All of the below methods should be coupled with the methods in calc client wire. ***/

	protected double calculateModulus(Map clientWireJSON) {
		return getPascalValue((Map) clientWireJSON.outerModulus) + getPascalValue((Map) clientWireJSON.coreModulus)
	}

	protected double calculatedExpansionCoefficient(Map clientWireJSON) {
		double outerExpansionCoefficientPerC = getCelsiusValue((Map) clientWireJSON.outerExpansionCoefficient)
		double coreExpansionCoefficientPerC = getCelsiusValue((Map) clientWireJSON.coreExpansionCoefficient)
		double outerModulusPa = getPascalValue((Map) clientWireJSON.outerModulus)
		double coreModulusPa = getPascalValue((Map) clientWireJSON.coreModulus)
		return (outerExpansionCoefficientPerC * (outerModulusPa / (outerModulusPa + coreModulusPa))) +
				(coreExpansionCoefficientPerC * (coreModulusPa / (outerModulusPa + coreModulusPa)))
	}

	private double getPascalValue(Map measurable) {
		if (measurable.unit == "POUND_FORCE_PER_SQUARE_INCH") {
			return (double) measurable.value * POUND_FORCE_PER_SQUARE_INCH_TO_PASCAL
		} else {
			return (double) measurable.value
		}
	}

	private double getCelsiusValue(Map measurable) {
		if (measurable.unit == "PER_FAHRENHEIT") {
			return (double) measurable.value * PER_FAHRENHEIT_TO_PER_CELSIUS
		} else {
			return (double) measurable.value
		}
	}

}
