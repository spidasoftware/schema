package com.spidasoftware.schema.conversion

//import com.spidasoftware.schema.json.CalcDBProjectComponent
import net.sf.json.JSONObject

/**
 * Represents a Project that exists in SPIDAdb.
 * User: pfried
 * Date: 1/23/14
 * Time: 12:38 PM
 * To change this template use File | Settings | File Templates.
 */
class CalcDBProject extends AbstractCalcDBComponent {

    CalcDBProject(JSONObject calcdbProjectJson) {
        super(calcdbProjectJson)
    }

    /**
     * @return the SPIDAdb ids of the locations contained in this project
     */
    List<String> getChildLocationIds(){
        return getJSON().getJSONArray("locations").collect { it.toString() }
    }

    @Override
    String toString() {
        return getName()?: "Project"
    }

}
