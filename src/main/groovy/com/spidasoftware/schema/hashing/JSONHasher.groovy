package com.spidasoftware.schema.hashing

import groovy.util.logging.Log4j
import net.sf.json.JSON
import net.sf.json.JSONObject
import net.sf.json.JSONArray
import net.sf.json.JSONSerializer
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.binary.Hex

@Log4j
class JSONHasher {

	public static String hash(String jsonString) {
		JSON json = JSONSerializer.toJSON(jsonString)
		return hash(json)
	}

	public static String hash(JSON json) {
		if (!json.isArray()) {
			json.remove('id')
			json.remove('version')
			json.remove('_id')
		}
		return DigestUtils.md5Hex(new CanonicalJSONSerializer().serialize(json))
	}

	public static String hash(HashMap map) {
		hash(JSONObject.fromObject(map))
	}

	public static String hash(List list) {
		hash(JSONArray.fromObject(list))
	}

}
