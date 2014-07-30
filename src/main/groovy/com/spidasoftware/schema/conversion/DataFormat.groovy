package com.spidasoftware.schema.conversion;

/**
 * Holds references to the various formats the calcdb supports
 */

public class DataFormat {

	/**
	 * Exchange file format. see Schema for details.
	 *
	 * Currently, this format is only supported for POST and PUT requests
	 */
	static final String EXCHANGE =  'exchange'

	/**
	 * represents data that strictly conforms to the calc project schema. Projects and Locations will include
	 * all of their child Locations/Designs, They will include Links to photos if any exist, but responses from
	 * CalcDB will not include the photo files themselves.
	 *
	 * All request types support this format.
	 */
	static final String CALC =  'calc'

	/**
	 * represents data in the raw format stored in the database. All project components are stored separately, and
	 * will contain references to their parent/child components. They will also contain various properties added by
	 * CalcDB.
	 *
	 * Only GET requests support this format.
	 */
	static final String REFERENCED = 'referenced'
}