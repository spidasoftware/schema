/*
 * ©2009-2021 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v9

import com.spidasoftware.schema.utils.StringFormatting

/**
 * Default environments prior to calc version 8.0
 */
enum Environment {
	ALLEY,
	COMMERCIAL_DRIVEWAY,
	HIGHWAY,
	FARM,
	OBSTRUCTED_PARALLEL_TO_STREET,
	PARALLEL_TO_STREET,
	PARKING_LOT,
	PEDESTRIAN,
	RAILROAD,
	RESIDENTIAL_DRIVEWAY,
	RURAL,
	STREET,
	UNLIKELY_PARALLEL_TO_STREET,
	WATER_WITHOUT_SAILBOATS,
	WATER_WITH_SAILBOATS

	@Override
	String toString() {
		return StringFormatting.makeReadableString(name())
	}
}
