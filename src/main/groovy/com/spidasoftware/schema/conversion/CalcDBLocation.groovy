package com.spidasoftware.schema.conversion

import groovy.transform.CompileStatic
import net.sf.json.JSONObject

/**
 * Represents a Location that exists in SPIDAdb.
 * User: pfried
 * Date: 1/23/14
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
@CompileStatic
class CalcDBLocation extends AbstractCalcDBComponent {

    /**
     * creates a new SPIDAdb Location using the JSON returned from the SPIDAdb api
     * @param locationJson
     */
    CalcDBLocation(JSONObject locationJson) {
        super(locationJson)
    }

    /**
     * gets a list of each of the Design ids belonging to this location that exist in SPIDAdb.
     * @return
     */
    public List<String> getDesignIds() {
        List<String> ids = new ArrayList<>()
        for (Object o : getJSON().getJSONArray("designs")) {
            ids.add((String) o)
        }
        return ids
    }

    @Override
    String toString(){
        return getName()?: "Location"
    }

    /**
     * gets a list of ids for this location's photos that exist in SPIDAdb
     * @return
     */
    public List<String> getPhotoIds() {
        List<String> ids = new ArrayList<>()

        if (getJSON().containsKey("images")) {
            for (Object image : getJSON().getJSONArray("images")) {
                JSONObject j = (JSONObject) image
                if (j.containsKey("link") && j.getJSONObject("link").containsKey("id")) {
                    ids.add(j.getJSONObject("link").getString("id"))
                }
            }
        }
        return ids
    }

    /**
     * @return the id of the parent project, if one exists. otherwise null
     */
    public String getParentProjectId() {
        if (getJSON()?.containsKey("projectId")) {
            return getJSON().getString("projectId")
        }
        return null
    }

    /**
     * @return the name of the parent project, if one exists. otherwise null
     */
    public String getParentProjectName(){
        if (getJSON()?.containsKey("projectName")) {
            return getJSON().getString("projectName")
        }
        return null
    }
}
