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
        return getJSON().getString("label")
    }

    @Override
    public String getCalcDBId() {
        try {
            return getJSON().getString("_id")
        } catch (JSONException e) {
            log.error("CalcDB Component does not have an _id")
        }
        return null
    }

    @Override
    public String getClientFileName() {
        try {
            return getJSON().getString("clientFile")
        } catch (JSONException e) {
            log.error("CalcDB Component does not have a ClientFile")
        }
        return null
    }

    @Override
    public Date getDateModified() {
        try {
            String s = getJSON().getString("dateModified")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            // this is here because java sucks at parsing ISO Dates and doesn't understand the 'Z' short hand
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
            return sdf.parse(s)
        } catch (JSONException e) {
            log.error("CalcDB Component does not have a dateModified");
        } catch (ParseException e) {
            log.error("Could not parse the dateModified field into a Date", e)
        }
        return null
    }

}
