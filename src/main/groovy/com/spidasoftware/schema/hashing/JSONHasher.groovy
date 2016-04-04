package com.spidasoftware.schema.hashing

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import org.apache.commons.codec.digest.DigestUtils

@Log4j
class JSONHasher {

	def static jsonSlurper = new JsonSlurper()

	static String hash(String jsonString){
		Map jsonObject = jsonSlurper.parseText(jsonString)
		jsonObject.remove('id')
		jsonObject.remove('version')
		jsonObject.remove('_id')
		def mapString = JsonOutput.toJson(sortJsonMap(jsonObject))
		return DigestUtils.md5Hex( mapString.toLowerCase() )
	}

	static String hash(Map jsonObject){
		//Do this to ensure and copy json
		return hash(JsonOutput.toJson(jsonObject))
	}

	//Partially recursive (uses sortAny() which also can call this function) sort of a map.
	static Map sortJsonMap(Map jsonMap){

		try {
			def id
			def version
			if (jsonMap.id != null){
				id = jsonMap.remove("id")
			}
			if (jsonMap.version != null){
				version = jsonMap.remove("version")
			}

			Map sortedMap = [:]

			if(id){
				sortedMap.put("id", id)
			}

			if(version){
				sortedMap.put("version", version)
			}

			sortedMap.putAll((Map) sortAny(jsonMap))

			return sortedMap
		} catch (NullPointerException e){
			log.error("Cannot pass null map", e)
		}
	}

	//Recursively sorts json data.  Partner with sortJsonMap
	public static Object sortAny(def data){

		if (data instanceof List){
			log.trace("sorting list:"+ data)
			def childrenSorted = data.collect {
				return sortAny(it)
			}
			childrenSorted = childrenSorted.sort {it.toString()}
			log.trace("sorted list:"+ childrenSorted)
			return childrenSorted
		} else if (data instanceof Map){
			log.trace("sorting map:"+ data)
			def key, value
			def sortedKeys = data.keySet().sort()
			def sortedMap = [:]
			for( int i = 0; i < data.size(); i++){
				key = sortedKeys[i]
				value = data[key]

				//Checks if the type of the value is BigDecimal, and then cuts it
				//to a floating point to avoid a MongoDB bug.
				if ( value instanceof BigDecimal ){
					value = (double) value.doubleValue()
				}
				sortedMap.put(key.toString(), sortAny(value))
			}
			log.trace("sorted map:"+ sortedMap)
			return sortedMap

		} else {

			return data
		}
	}

}
