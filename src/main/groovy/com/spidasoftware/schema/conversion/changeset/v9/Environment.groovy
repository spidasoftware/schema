/*
 * ©2009-2021 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v9

import com.spidasoftware.schema.utils.StringFormatting

/**
 * Default environments prior to calc version 8.0
 */
enum Environment {

	STREET,
	HIGHWAY,
	PEDESTRIAN,
	PARALLEL_TO_STREET,
	OBSTRUCTED_PARALLEL_TO_STREET,
	UNLIKELY_PARALLEL_TO_STREET,
	RESIDENTIAL_DRIVEWAY,
	COMMERCIAL_DRIVEWAY,
	PARKING_LOT,
	ALLEY,
	RAILROAD,
	RURAL,
	FARM,
	WATER_WITH_SAILBOATS,
	WATER_WITHOUT_SAILBOATS

	@Override
	String toString() {
		return StringFormatting.makeReadableString(name())
	}
}
