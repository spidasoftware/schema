package com.spidasoftware.schema.conversion

import groovy.transform.CompileStatic

/**
 * Represents a Location that exists in SPIDA DB.
 */
@CompileStatic
class SpidaDBLocation extends AbstractSpidaDBComponent {

    /**
     * creates a new SPIDA DB Location using the JSON returned from the SPIDA DB api
     */
    SpidaDBLocation(Map locationJson) {
        super(locationJson)
    }

    void updateLocationIds(Map<String, String> oldToNew){

        def id = getMap().get("id")
        if(oldToNew.get(id)){
            getMap().put("id", oldToNew.get(id) )
        }

        def calcId = getCalcJSON().get("id")
        if(oldToNew.get(calcId)){
            getCalcJSON().put("id", oldToNew.get(calcId) )
        }

    }

    /**
     * gets a list of each of the Design ids belonging to this location that exist in SPIDA DB.
     * @return
     */
    public List<String> getDesignIds() {
        getCalcJSON().get('designs')?.collect{ Map design -> design.id}
    }

	@Override
	Map getCalcJSON() {
		return getMap().get('calcLocation')
	}

    @Override
    String toString(){
        return getName()?: "Location without a label"
    }

    /**
     * gets a list of ids for this location's photos that exist in SPIDA DB
     * @return
     */
    public List<String> getPhotoIds() {
        List<String> ids = new ArrayList<>()

        if (getCalcJSON().containsKey("images")) {
            for (Object image : getCalcJSON().get("images")) {
                Map j = (Map) image
                if (j.containsKey("link") && ((Map) j.get("link")).containsKey("id")) {
                    ids.add((String) ((Map) j.get("link")).get("id"))
                }
            }
        }
        return ids
    }

    /**
     * @return the id of the parent project, if one exists. otherwise null
     */
    public String getParentProjectId() {
        getMap().get("projectId")
    }

    /**
     * @return the name of the parent project, if one exists. otherwise null
     */
    public String getParentProjectName(){
	    getMap().get('projectLabel')
    }
}
