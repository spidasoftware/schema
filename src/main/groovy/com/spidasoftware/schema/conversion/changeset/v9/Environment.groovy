/*
 * ©2009-2021 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v9


import org.apache.commons.lang.WordUtils
import org.apache.commons.lang3.StringUtils

/**
 * Default environments prior to calc version 8.0
 */
enum Environment {

	NONE,
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
		return makeReadableString(name())
	}
	
	/**
	 * Convert all upper case string to a capitalized string with underscores removed.
	 *
	 * @return the formatted string
	 */
	public static String makeReadableString(String enumerableString) {
		char ch;       // One of the characters in str.
		char prevCh = '.';   // The character that comes before ch in the string.
		char[] chars = new char[enumerableString.length()];
		for (int i = 0; i < enumerableString.length(); i++) {
			ch = enumerableString.charAt(i);
			if (Character.isLetter(ch) && !Character.isLetter(prevCh)) {
				chars[i] = Character.toUpperCase(ch);
			} else if (Character.isLetter(ch)) {
				chars[i] = Character.toLowerCase(ch);
			} else if (ch == '_') {
				chars[i] = ' ';
			} else {
				chars[i] = (ch);
			}
			prevCh = ch;
		}
		return new String(chars);
	}

	static boolean isNoneEnvironment(String nameOfEnvironment) {
		return equalsIgnoreCaseAndEnumFormat(NONE.name(), nameOfEnvironment)
	}

	static Environment getEnvironment(String nameOfEnvironment) {
		return values().find {equalsIgnoreCaseAndEnumFormat(it.name(), nameOfEnvironment)}
	}

	static boolean equalsIgnoreCaseAndEnumFormat(String a, String b) {
		return makeReadableString(a).equalsIgnoreCase(makeReadableString(b));
	}
}
