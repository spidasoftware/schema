/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion

import groovy.transform.CompileStatic

/**
 * Represents a Design that exists in SPIDAdb
 */
@CompileStatic
class SpidaDBDesign extends AbstractSpidaDBComponent {


    public SpidaDBDesign(Map json) {
        super(json)
    }

    @Override
    String toString(){
        return getName()?: "Design"
    }

	void updateLocationIds(Map<String, String> oldToNew){

		def locationId = getMap().get("locationId")
		if(oldToNew.get(locationId)){
			getMap().put("locationId", oldToNew.get(locationId) )
		}

	}

    /**
     * returns the JSONObject representing the worst analysis result
     * @return JSONObject in the format created by ResultJSON class, or null if it's not found
     */
    public Map getWorstPoleLoadingResult() {
        return getWorstCaseAnalysisResults()?.get('pole')
    }

	public Map getWorstCaseAnalysisResults(){
		return getMap().get('worstCaseAnalysisResults')
	}

    /**
     * returns the name of this design's parent location, if it has one.
     * otherwise returns null
     * @return
     */
    public String getParentLocationName() {
	    getMap().get("locationLabel")
    }

    /**
     * returns the SpidaDB _id of this design's parent location if it has one.
     * otherwise returns null
     * @return
     */
    public String getParentLocationId() {
	    getMap().get("locationId")
    }

	@Override
	Map getCalcJSON() {
		return getMap().get('calcDesign')
	}

    /**
     * returns the name of this design's parent project if it has one.
     * otherwise returns null.
     * @return
     */
    public String getParentProjectName() {
	    getMap().get("projectLabel")
    }

    /**
     * returns the _id of the parent project
     * @return
     */
    String getParentProjectId() {
	    getMap().get("projectId")
    }

	SpidaDBResult getResult(){
		Map calcDesign = getMap().get("calcDesign")
		return calcDesign.get("calcResult")
	}

}
