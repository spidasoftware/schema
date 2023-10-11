/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion

import com.spidasoftware.schema.util.VirtualMap
import groovy.util.logging.Slf4j

/**
 * Abstract class for SPIDAdb project components that handles some of the boiler plate stuff
 */
@Slf4j
abstract class AbstractSpidaDBComponent implements SpidaDBProjectComponent {
    VirtualMap json

    public AbstractSpidaDBComponent(Map json){
        this.json = json
    }

    @Override
    public Map getMap() {
        return this.json
    }

    @Override
    public void setMap(Map json) {
        this.json = json
    }

    @Override
    public String getName() {
        return getCalcJSON().get("label")
    }

    @Override
    public String getSpidaDBId() {
	    return getMap().get("id")
    }

    @Override
    public String getClientFileName() {
	    return getMap().get("clientFile")
    }

    @Override
    public Date getDateModified() {
	    long time = json.get('dateModified')
	    return new Date(time)
    }

	/**
	 * returns the json object representing the user, if one exists, otherwise null.
	 * json will have:
	 *   'id': the user id stored as a string
	 *   'email': the user email stored as a string
	 * these properties may be set to default values if the component was created by an unauthenticated
	 * source, such as project manager.
	 *
	 * @return
	 */
	Map getUser(){
		return getMap().user
	}

	/**
	 * sets the 'user' property with the given values
	 * @param id
	 * @param email
	 */
	void setUser(String id, String email) {
		Map user = [:]
		user.put('id', id)
		user.put('email', email)
		getMap().put('user', user)
	}

	/**
	 *
	 * @param schema
	 */
	void setSchema(String schema) {
		Map map = new HashMap(getMap())
		if (map?.get(getCalcJSONName())) {
			map.get(getCalcJSONName())["schema"] = schema
		}
		getMap().putAll(map)
	}

	/**
	 *
	 * @param version
	 */
	void setVersion(String version) {
		Map map = new HashMap(getMap())
		if (map?.get(getCalcJSONName())) {
			map.get(getCalcJSONName())["version"] = version
		}
		getMap().putAll(map)
	}

	abstract Map getCalcJSON()

	abstract String getCalcJSONName()
}
