/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset

import com.fasterxml.jackson.databind.ObjectMapper

abstract class ChangeSet {
	/*
	 ** deep copy for json
	 */
	static Map duplicateAsJson(Map map){
		ObjectMapper mapper = new ObjectMapper()
		return mapper.readValue(mapper.writeValueAsString(map), Map)
	}
}