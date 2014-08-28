package com.spidasoftware.schema.conversion

import net.sf.json.JSONException
import net.sf.json.JSONObject
import org.apache.log4j.Logger

import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Abstract class for SPIDAdb project components that handles some of the boiler plate stuff
 * User: pfried
 * Date: 2/10/14
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCalcDBComponent implements CalcDBProjectComponent {
    private static final Logger log = Logger.getLogger(AbstractCalcDBComponent.class)
    JSONObject json

    public AbstractCalcDBComponent(JSONObject json){
        this.json = json
    }

    @Override
    public JSONObject getJSON() {
        return this.json
    }

    @Override
    public void setJSON(JSONObject json) {
        this.json = json
    }

    @Override
    public String getName() {
        return getCalcJSON().optString("label")
    }

    @Override
    public String getCalcDBId() {
	    return getJSON().getString("id")
    }

    @Override
    public String getClientFileName() {
	    return getJSON().getString("clientFile")
    }

    @Override
    public Date getDateModified() {
	    long time = json.getLong('dateModified')
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
	JSONObject getUser(){
		return getJSON().optJSONObject('user')
	}

	/**
	 * sets the 'user' property with the given values
	 * @param id
	 * @param email
	 */
	void setUser(String id, String email) {
		JSONObject user = new JSONObject()
		user.elementOpt('id', id)
		user.elementOpt('email', email)
		getJSON().put('user', user)
	}

	abstract JSONObject getCalcJSON()

}
