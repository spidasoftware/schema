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

	/*****************************************
	 *    The below methods have nothing     *
	 * to do with the hashing methods above. *
	 * They were used by a previous          *
	 * implementation of this class and are  *
	 * also used outside of this class by    *
	 * calcDB. So, they need to remain in    *
	 * this class until the calcDB code can  *
	 * be refactored.                        *
	 *****************************************/

	//Partially recursive (uses sortAny() which also can call this function) sort of a map.
	static Map sortJsonMap(Map jsonMap) {

		def id
		def version
		if (jsonMap.id != null) {
			id = jsonMap.remove("id")
		}
		if (jsonMap.version != null) {
			version = jsonMap.remove("version")
		}

		Map sortedMap = [:]

		if (id) {
			sortedMap.put("id", id)
		}

		if (version) {
			sortedMap.put("version", version)
		}

		sortedMap.putAll((Map) sortAny(jsonMap))

		return sortedMap

	}

	//Recursively sorts json data.  Partner with sortJsonMap
	public static Object sortAny(def data) {

		if (data instanceof List) {
			log.trace("sorting list:" + data)
			def childrenSorted = data.collect {
				return sortAny(it)
			}
			childrenSorted = childrenSorted.sort { it.toString() }
			log.trace("sorted list:" + childrenSorted)
			return childrenSorted
		} else if (data instanceof Map) {
			log.trace("sorting map:" + data)
			def key, value
			def sortedKeys = data.keySet().sort()
			def sortedMap = [:]
			for (int i = 0; i < data.size(); i++) {
				key = sortedKeys[i]
				value = data[key]
				//Checks if the type of the value is BigDecimal, and then cuts it
				//to a floating point to avoid a MongoDB bug with BDs.
				if (value instanceof BigDecimal) {
					log.trace("Found Big Decimal, converting to a double")
					value = (double) value.doubleValue()
				}
				sortedMap.put(key.toString(), sortAny(value))
			}
			log.trace("sorted map:" + sortedMap)
			return sortedMap

		} else {

			return data
		}
	}
}
