package com.spidasoftware.schema.changesets

import net.sf.json.JSONObject

/**
 * interface for a json change set to convert between versions of the api
 */
interface ChangeSet {

	String getSchemaPath()

	void convert(JSONObject jsonObject)

}