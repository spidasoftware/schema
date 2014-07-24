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
        getStringProperty("locationName")
    }

    /**
     * returns the CalcDB _id of this design's parent location if it has one.
     * otherwise returns null
     * @return
     */
    public String getParentLocationId() {
        getStringProperty("locationId")
    }

    /**
     * returns the name of this design's parent project if it has one.
     * otherwise returns null.
     * @return
     */
    public String getParentProjectName() {
        getStringProperty("projectName")
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
