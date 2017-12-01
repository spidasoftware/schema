package com.spidasoftware.schema.conversion

import groovy.transform.CompileStatic

/**
 * Common interface for all components stored in SPIDAdb.
 */
@CompileStatic
public interface SpidaDBProjectComponent {

    /**
     * gets the json object associated with this component. This is the same json that is stored in SPIDAdb
     * @return
     */
    public Map getMap()

    public void setMap(Map json)

    /**
     * @return the name of the component. This matches the displayed id of the component in Calc
     */
    public String getName()

    /**
     * @return the id of the component in SPIDAdb. REST api calls to SPIDAdb use this
     */
    public String getSpidaDBId()

    /**
     * gets the name of the client file used to create this item. Currently (3/31/14) this only gets set when an
     * entire project is saved to SPIDAdb. It will be unspecified if a Location or Design is saved without a parent project
     * @return
     */
    public String getClientFileName()

    /**
     * @return the Date last modified from SPIDAdb
     */
    public Date getDateModified()
}
