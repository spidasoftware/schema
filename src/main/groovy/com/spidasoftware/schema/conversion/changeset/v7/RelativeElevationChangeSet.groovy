/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v7

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset

import static java.lang.Math.sin

/**
 * v7.3 adds relative elevation to wireEndPoints. Removes "relative elevation" when we down convert.
 */
class RelativeElevationChangeSet extends AbstractCalcDesignChangeset {

	protected double footMetreSiConversion = 0.3048d
	protected double radianDegreeSiConversion = 57.29577951308232d

	double convertToMetre(double foot) {
		return foot * footMetreSiConversion
	}

	double convertToRadian(double degree) {
		return degree / radianDegreeSiConversion
	}

	double calculateInclination(double relativeElevation, double distance) {
		return Math.asin(relativeElevation/distance as double)
	}

	double calculateRelativeElevation(double inclinationRad, double distance) {
		double sinInclination = sin(inclinationRad)
		return distance * sinInclination
	}

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		def wireEndPoints = designJSON.structure?.wireEndPoints
		wireEndPoints.each { Map wireEndPoint ->

			def inclinationMap = wireEndPoint.get("inclination") as Map
			def distanceMap = wireEndPoint.get("distance") as Map
			if (distanceMap != null && inclinationMap != null) {

				double inclination = inclinationMap.get("value") as double
				double distance = distanceMap.get("value") as double

				if (inclinationMap.get("unit") == "DEGREE_ANGLE") {
					inclination = convertToRadian(inclination)
				}

				if (distanceMap.get("unit") == "FOOT") {
					distance = convertToMetre(distance)
				}

				double relativeElevation = calculateRelativeElevation(inclination, distance)
				wireEndPoint.put("relativeElevation", [unit:"METRE", value:relativeElevation])
				wireEndPoint.remove("inclination")
			}
		}
	}

	/**
	 * If there are detailed results, add them to the "analysis" section.  Otherwise, add resultId to the "analysis" section.
	 * Remove "analysisDetails" section.
	 */
	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		def wireEndPoints = designJSON.structure?.wireEndPoints
		wireEndPoints.each { Map wireEndPoint ->

			def relativeElevationMap = wireEndPoint.get("relativeElevation") as Map
			def distanceMap = wireEndPoint.get("distance") as Map
			if (distanceMap != null && relativeElevationMap != null) {

				double relativeElevation = relativeElevationMap.get("value") as double
				double distance = distanceMap.get("value") as double

				if (relativeElevationMap.get("unit") == "FOOT") {
					relativeElevation = convertToMetre(relativeElevation)
				}

				if (distanceMap.get("unit") == "FOOT") {
					distance = convertToMetre(distance)
				}

				double inclination = calculateInclination(relativeElevation, distance)
				wireEndPoint.put("inclination", [unit:"RADIAN", value:inclination])
				wireEndPoint.remove("relativeElevation")
			}
		}
	}
}
