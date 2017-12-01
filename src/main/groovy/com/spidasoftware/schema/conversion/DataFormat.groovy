package com.spidasoftware.schema.conversion;

/**
 * Holds references to the various formats the spidadb supports
 */

public enum DataFormat {

	/**
	 * Exchange file format. see Schema for details.
	 *
	 * Currently, this format is only supported for POST and PUT requests
	 */
	EXCHANGE('exchange'),

	/**
	 * represents data that strictly conforms to the calc project schema. Projects and Locations will include
	 * all of their child Locations/Designs, They will include Links to photos if any exist, but responses from
	 * SpidaDB will not include the photo files themselves.
	 *
	 * All request types support this format.
	 */
	CALC("calc"),

	/**
	 * represents data in the raw format stored in the database. All project components are stored separately, and
	 * will contain references to their parent/child components. They will also contain various properties added by
	 * SpidaDB.
	 *
	 * Only GET requests support this format.
	 */
	REFERENCED("referenced")

	String value

	DataFormat(String val){
		this.value = val
	}



	static DataFormat fromString(String str) {
		for (DataFormat df : values()) {
			if (df.getValue().equalsIgnoreCase(str)) {
				return df
			}
		}
		throw new IllegalArgumentException("No DataFormat exists for String: ${str}")
	}

	@Override
	String toString() {
		return value
	}
}