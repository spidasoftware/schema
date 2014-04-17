package com.spidasoftware.schema.changesets

import net.sf.json.JSON

/**
 * interface for a json change set to convert between versions of the api
 */
interface ChangeSet {

	String getSchemaVersion()

	String getSchemaPath()

	void convert(JSON jsonObject) //Could be a JSONArray or JSONObject

}