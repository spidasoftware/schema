package com.spidasoftware.schema.conversion

import net.sf.json.JSONObject

/**
 * Created by msaalfeld on 7/15/14.
 */
public interface FormatConverter {
    Collection<CalcDBProjectComponent> convertCalcProject(JSONObject calcProject)
    Collection<CalcDBProjectComponent> convertCalcLocation(JSONObject calcLocation, JSONObject calcProject)
    CalcDBDesign convertCalcDesign(JSONObject calcDesign, JSONObject calcLocation, JSONObject calcProject)

    JSONObject convertCalcDBProject(CalcDBProject calcDBProject, Collection<CalcDBLocation> calcDBLocations, Collection<CalcDBDesign> calcDBDesigns)
    JSONObject convertCalcDBLocation(CalcDBLocation calcDBLocation, Collection<CalcDBDesign> calcDBDesigns)
    JSONObject convertCalcDBDesign(CalcDBDesign design)
}
