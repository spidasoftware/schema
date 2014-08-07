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
        return getCalcJSON().getString("label")
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

	abstract JSONObject getCalcJSON()

}
