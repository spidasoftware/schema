package com.spidasoftware.schema.hashing

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import org.apache.commons.codec.digest.DigestUtils

import java.security.MessageDigest

@Log4j
class JSONHasher {

	def static jsonSlurper = new JsonSlurper()

	private static String hashJsonString(String jsonString) {
		def jsonObject = jsonSlurper.parseText(jsonString)
		def mapString
		if (jsonObject instanceof List) {
			mapString = JsonOutput.toJson(sortAny(jsonObject))
		} else {
			jsonObject.remove('id')
			jsonObject.remove('version')
			jsonObject.remove('_id')
			mapString = JsonOutput.toJson(sortJsonMap(jsonObject))
		}
		return DigestUtils.md5Hex(mapString)
	}

	static String hash(List jsonList) {
		return hashJsonString(JsonOutput.toJson(jsonList))
	}

	static String hash(Map jsonObject) {
		return hashJsonString(JsonOutput.toJson(jsonObject))
	}

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
