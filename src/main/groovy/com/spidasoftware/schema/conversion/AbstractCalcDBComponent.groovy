package com.spidasoftware.schema.conversion

import org.apache.log4j.Logger

/**
 * Abstract class for SPIDAdb project components that handles some of the boiler plate stuff
 */
abstract class AbstractCalcDBComponent implements CalcDBProjectComponent {
    private static final Logger log = Logger.getLogger(AbstractCalcDBComponent.class)
    Map json

    public AbstractCalcDBComponent(Map json){
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
    public String getCalcDBId() {
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
	 *   'email': the email address
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

	abstract Map getCalcJSON()

}
