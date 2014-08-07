package com.spidasoftware.schema.conversion

import groovy.transform.CompileStatic
import net.sf.json.JSONObject

/**
 * Represents a Design that exists in SPIDAdb
 * User: pfried
 * Date: 1/23/14
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
@CompileStatic
class CalcDBDesign extends AbstractCalcDBComponent {


    public CalcDBDesign(JSONObject json) {
        super(json)
    }

    @Override
    String toString(){
        return getName()?: "Design"
    }

    /**
     * returns the JSONObject representing the worst analysis result
     * @return JSONObject in the format created by ResultJSON class, or null if it's not found
     */
    public JSONObject getWorstPoleLoadingResult() {
        JSONObject worstResult = null
        if (getJSON()?.containsKey("worstPoleResult")) {
            worstResult = getJSON().getJSONObject("worstPoleResult")
        }
        return worstResult
    }

    /**
     * returns the name of this design's parent location, if it has one.
     * otherwise returns null
     * @return
     */
    public String getParentLocationName() {
        getStringProperty("locationLabel")
    }

    /**
     * returns the CalcDB _id of this design's parent location if it has one.
     * otherwise returns null
     * @return
     */
    public String getParentLocationId() {
        getStringProperty("locationId")
    }

	@Override
	JSONObject getCalcJSON() {
		return getJSON().getJSONObject('calcDesign')
	}

    /**
     * returns the name of this design's parent project if it has one.
     * otherwise returns null.
     * @return
     */
    public String getParentProjectName() {
        getStringProperty("projectLabel")
    }

    /**
     * returns the _id of the parent project
     * @return
     */
    String getParentProjectId() {
        getStringProperty("projectId")
    }

    private String getStringProperty(String propertyName) {
        String value = null
        if (getJSON().containsKey(propertyName)) {
            value = getJSON().getString(propertyName)
        }
        return value
    }

}
