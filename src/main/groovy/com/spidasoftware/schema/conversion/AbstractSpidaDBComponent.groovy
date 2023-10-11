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

	// file-backed json map for the component
    private VirtualMap json

	/**
	 * Constructor - once created, the map can only be changed using methods in this class hierarchy.
	 * Direct updates on the json map are not permitted.
	 * @param json
	 */
    public AbstractSpidaDBComponent(Map json){
        this.json = json
    }

	/**
	 * gets an immutable map for the component
	 * @return an immutable map for the component
	 */
    @Override
    public final Map getMap() {
        return this.json.asImmutable()
    }

	/**
	 * gets a mutable map for the component
	 * @return a mutable map for the component
	 */
	protected final Map getInternalMap() {
		return this.json
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
		getInternalMap().put('user', user)
	}

	/**
	 * sets the 'schema' key value in the map
	 * @param schema
	 */
	void setSchema(String schema) {
		update("schema", schema)
	}

	/**
	 * sets the 'version' key value in the map
	 * @param version
	 */
	void setVersion(String version) {
		update("version", version)
	}

	/**
	 * sets the 'clientFileId' key value in the map
	 * @param clientFileId
	 */
	void setClientFileId(String clientFileId) {
		update("clientFileId", clientFileId)
	}

	/**
	 * set the key as value in the internal map
	 * @param key
	 * @param value
	 */
	private void update(Object key, Object value) {
		Map map = new HashMap(getInternalMap())
		if (map?.get(getCalcJSONName())) {
			map.get(getCalcJSONName())[key] = value
		}
		getInternalMap().putAll(map)
	}

	abstract Map getCalcJSON()
	abstract String getCalcJSONName()
}
