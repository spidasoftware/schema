/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion

/**
 * Represents a Project that exists in SPIDAdb.
 */
class SpidaDBProject extends AbstractSpidaDBComponent {

    SpidaDBProject(Map spidadbProjectJson) {
        super(spidadbProjectJson)
    }

    /**
     * @return the SPIDAdb ids of the locations contained in this project
     */
    List<String> getChildLocationIds(){
        return getCalcJSON().get("leads")?.collect{Map lead-> lead.locations}?.flatten()?.collect {Map location-> location.id }
    }

    void updateLocationIds(Map<String, String> oldToNew){
      Map map = new HashMap(getMap())
      map.calcProject.leads.each{Map lead ->
           lead.locations.each{Map location ->
	           if(oldToNew.get(location.id)){
		           location.put("id", oldToNew.get(location.id))
	           }
           }
      }
      getMap().putAll(map)
    }

	@Override
	String getClientFileName() {
		return getCalcJSON().get('clientFile')
	}

	@Override
	Map getCalcJSON() {
		return getMap().get('calcProject')
	}

	@Override
    String toString() {
        return getName()?: "Project"
    }

}
