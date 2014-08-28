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
        return getWorstCaseAnalysisResults().optJSONObject('pole')
    }

	public JSONObject getWorstCaseAnalysisResults(){
		return getJSON().optJSONObject('worstCaseAnalysisResults')
	}

    /**
     * returns the name of this design's parent location, if it has one.
     * otherwise returns null
     * @return
     */
    public String getParentLocationName() {
	    getJSON().optString("locationLabel")
    }

    /**
     * returns the CalcDB _id of this design's parent location if it has one.
     * otherwise returns null
     * @return
     */
    public String getParentLocationId() {
	    getJSON().optString("locationId")
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
	    getJSON().optString("projectLabel")
    }

    /**
     * returns the _id of the parent project
     * @return
     */
    String getParentProjectId() {
	    getJSON().optString("projectId")
    }

}
