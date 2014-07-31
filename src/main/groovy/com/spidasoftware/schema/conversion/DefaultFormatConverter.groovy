package com.spidasoftware.schema.conversion

import net.sf.json.JSONObject
import net.sf.json.JSONArray
import org.apache.log4j.Logger
import org.bson.types.ObjectId

import java.text.ParseException
import java.text.SimpleDateFormat

class DefaultFormatConverter implements FormatConverter {
    private static final def log = Logger.getLogger(this)

    Collection<CalcDBProjectComponent> convertCalcProject(JSONObject calcProject) {
        List<CalcDBProjectComponent> components = []
        addDBIdsToProject(calcProject)
        JSONObject convertedProject = JSONObject.fromObject(calcProject)
        convertedProject.put("dateModified", new Date().time)
        if (calcProject.containsKey("projectForms")) {
            convertedProject.put("projectForms", calcProject.get("projectForms"))
        }

        convertedProject.remove("schema")
        int locationCount = 0
        JSONArray newLocations = new JSONArray()
        calcProject.leads.each { lead ->
            lead.locations.each { location ->
                components.addAll(convertCalcLocation(location, calcProject))
                newLocations.add(location["_id"].toString())
                locationCount++
            }
        }
        convertedProject.remove("leads")
        convertedProject.put("locations", newLocations)
        convertedProject.put("locationCount", locationCount)
        components.add(new CalcDBProject(convertedProject))
        return components
    }

    Collection<CalcDBProjectComponent> convertCalcLocation(JSONObject calcLocation, JSONObject calcProject) {
        ArrayList<CalcDBProjectComponent> components = []
        addDBIdsToLocation(calcLocation)
        JSONObject convertedLocation = JSONObject.fromObject(calcLocation)
        JSONArray designList = new JSONArray()
        convertedLocation.get("designs").each { design ->
            components.add(convertCalcDesign(design, calcLocation, calcProject))
            designList.add(design["_id"].toString())
        }
        convertedLocation.remove("designs") //get rid of the old designs
        convertedLocation.put("designs", designList)  //replace with the array of design_id's
        if (calcProject) {
            convertedLocation.put("projectLabel", calcProject.getString("label"))
            convertedLocation.put("projectId", calcProject.get("_id").toString())
            convertedLocation.put("clientFile", calcProject.getString("clientFile"))
            convertedLocation.put("clientFileVersion", calcProject.getString("clientFileVersion"))
        }
        convertedLocation.put("dateModified", new Date().time)
        components.add(new CalcDBLocation(convertedLocation))
        return components
    }

    CalcDBDesign convertCalcDesign(JSONObject calcDesign, JSONObject calcLocation = null, JSONObject calcProject = null) {
        addDBIdToDesign(calcDesign)
        JSONObject convertedDesign = JSONObject.fromObject(calcDesign)

        if (calcLocation){
            convertedDesign.put("locationLabel", calcLocation.get("label").toString())
            convertedDesign.put("locationId", calcLocation.get("_id").toString())
        }
        if (calcProject){
            convertedDesign.put("projectLabel", calcProject.getString("label"))
            convertedDesign.put("projectId", calcProject.get("_id").toString())
            convertedDesign.put("clientFile", calcProject.getString("clientFile"))
            convertedDesign.put("clientFileVersion", calcProject.get("clientFileVersion"))
        }

        convertedDesign.put("dateModified", new Date().time)

        //We're going to rearrange these properties in the new design object, so remove them
        convertedDesign.remove("analysis")
        //add analysis results to each component
        addAnalysisResultsToNewDesign(calcDesign, convertedDesign)

        return new CalcDBDesign(convertedDesign)
    }

    private void addAnalysisResultsToNewDesign(JSONObject originalDesignObject, JSONObject newDesignObject) {
        if (!originalDesignObject.containsKey("analysis")){
            log.trace("Design: ${originalDesignObject.label} does not contain any analysis results")
            return
        }
        List allOriginalAnalysisResults = originalDesignObject.analysis
        // Pole is a special Case, dealt with separately
        def analyzableComponentTypes = [
                "anchors",
                "guys",
                "spanGuys",
                "crossArms",
                "insulators",
                "pushBraces",
                "sidewalkBraces"
        ]
        analyzableComponentTypes.each { String type ->
            def componentList = newDesignObject.structure."$type"
            if (componentList && !componentList.isEmpty()){

                List allResultsForType = []
                componentList.each { component ->
                    List componentResults = getResultsForComponent(component.id, allOriginalAnalysisResults)
                    component.put("analysisResults", componentResults)
                    allResultsForType.addAll(componentResults)
                }

                def worstResultForType = getWorstResult(allResultsForType)
                if (worstResultForType){
                    // generate the property name for the new design json (i.e. 'worstAnchorResult')
                    def propName = type[0..-2].capitalize()
                    newDesignObject.put("worst${propName}Result", worstResultForType)
                }
            }

        }

        // now to get the results for the Pole
        List allPoleResults = collectPoleResults(allOriginalAnalysisResults)

        if (!allPoleResults.isEmpty()){
            newDesignObject.put("worstPoleResult", getWorstResult(allPoleResults))
            newDesignObject.structure.pole.analysisResults = allPoleResults
        } else {
            log.warn("Design ${originalDesignObject.label} has analysis results, but no results exist for the Pole")
        }
    }

    private List collectPoleResults(List originalAnalysisResults){
        List allPoleResults = []
        originalAnalysisResults.each { loadCase ->
            def poleResults = loadCase.results.findAll { it.component.startsWith("Pole") }
            allPoleResults.addAll(poleResults)
        }
        return allPoleResults
    }

    private JSONObject getWorstResult(List resultsArray){
        JSONObject worstResult = null
        double worstNormalizedResult = Double.MAX_VALUE
        resultsArray.each { result ->
            /*
            * Since results can be either SF or PERCENT, we'll have to check the unit
            * before we compare the relative 'badness' of each result (how does the actual
            * compare to the allowable). Strength results are a special case because even
            * though the unit is PERCENT, lower numbers mean a worse result. This is the
            * opposite of any other % based analysis result.
            */
            def normalizedResult
            if (result.unit == "SF" || result.component == "Pole-Strength"){
                normalizedResult = result.getDouble("actual") / result.getDouble("allowable")
            } else {
                // unit is PERCENT
                normalizedResult = result.getDouble("allowable") / result.getDouble("actual")
            }

            if (normalizedResult < worstNormalizedResult){
                worstNormalizedResult = normalizedResult
                worstResult = JSONObject.fromObject(result)
            }
        }
        return worstResult
    }


    private JSONArray getResultsForComponent(String componentId, List analysisList) {
        JSONArray results = new JSONArray()
        analysisList.each { loadCase ->
            loadCase.get("results").each { result ->
                JSONObject resultObject = (JSONObject) result
                if (resultObject.getString("component") == componentId) {
                    results.add(resultObject)
                }
            }
        }
        return results
    }

    private Map addDBIdsToProject(Map calcProject){
        calcProject.leads.each { lead ->
            lead.locations.each { location ->
                addDBIdsToLocation(location)
            }
        }
        return addDBId(calcProject)
    }

    private Map addDBIdsToLocation(Map calcLocation) {
        calcLocation.designs.each { design ->
            addDBIdToDesign(design)
        }
        return addDBId(calcLocation)
    }

    private Map addDBIdToDesign(Map design){
        return addDBId(design)
    }

    private Map addDBId(Map thing) {
        if (!thing.containsKey("_id")){
            thing."_id" = newPrimaryKey()
        }
        return thing
    }

    JSONObject convertCalcDBProject(CalcDBProject calcDBProject, Collection<CalcDBLocation> calcDBLocations, Collection<CalcDBDesign> calcDBDesigns) {
        Map<String, CalcDBLocation> calcDBLocationMap = buildCalcDBIdMap(calcDBLocations)
        Map<String, CalcDBDesign> calcDBDesignMap = buildCalcDBIdMap(calcDBDesigns)
        return convertCalcDBProject(calcDBProject, calcDBLocationMap, calcDBDesignMap)
    }

    JSONObject convertCalcDBProject(CalcDBProject calcDBProject, Map<String,CalcDBLocation> calcDBLocationMap, Map<String, CalcDBDesign> calcDBDesignMap) {
        // new project json object that we can keep adding to
        JSONObject convertedProject = JSONObject.fromObject(calcDBProject.getJSON())
        convertedProject.schema = "/v1/schema/spidacalc/project.schema"

        convertedProject.remove("locations")

        JSONArray locationsArray = new JSONArray()

        // first match up projects with their child locations and designs
        List<String> locationIds = calcDBProject.getChildLocationIds()

        locationIds.each { String locationId ->
            CalcDBLocation calcDBLocation = calcDBLocationMap.get(locationId)
            if (calcDBLocation == null) {
                log.debug("Skipping CalcDBLocation: " + locationId + " because it was not selected")
            } else {
                // remove the location from the map so it doesn't get in there twice
                calcDBLocationMap.remove(locationId)
                locationsArray.add(convertCalcDBLocation(calcDBLocation, calcDBDesignMap))
            }
        }

        // now go through whatever locations and designs are left (not members of a selected project
        calcDBLocationMap.values().each { CalcDBLocation location ->
            JSONObject locationJson = convertCalcDBLocation(location, calcDBDesignMap)
            locationsArray.add(locationJson)
        }

        // finally, for whatever designs are left without a location, add the design to a new location
        List<CalcDBDesign> designsToRemove = []
        calcDBDesignMap.values().each { CalcDBDesign design ->
            log.debug("Adding orphaned CalcDB Design: " + design.calcDBId)
            JSONObject locationFromDesign = createLocationJsonForDesign(design)
            designsToRemove.add(design)
            calcDBDesignMap.remove(design.calcDBId)
            locationsArray.add(locationFromDesign)
        }
        JSONObject lead = new JSONObject()
        lead.put("locations", locationsArray)
        lead.put("id", "Lead")

        JSONArray leadsArray = new JSONArray()
        leadsArray.add(lead)
        convertedProject.put("leads", leadsArray)

        ["_id", "dateModified", "locationCount"].each { convertedProject.remove(it) }
        return convertedProject
    }

    JSONObject convertCalcDBLocation(CalcDBLocation calcDBLocation, Collection<CalcDBDesign> calcDBDesigns) {
        Map<String, CalcDBDesign> calcDBDesignMap = buildCalcDBIdMap(calcDBDesigns)
        return convertCalcDBLocation(calcDBLocation, calcDBDesignMap)
    }

    JSONObject convertCalcDBLocation(CalcDBLocation calcDBLocation, Map<String, CalcDBDesign> calcDBDesignMap) {
        log.debug("Adding CalcDBLocation: " + calcDBLocation.getName())
        JSONObject convertedLocation = JSONObject.fromObject(calcDBLocation.getJSON())
        convertedLocation.remove("designs")
        JSONArray designs = new JSONArray()
        calcDBLocation.getDesignIds().each { String designId ->
            CalcDBDesign calcDBDesign = calcDBDesignMap.get(designId)
            if (calcDBDesign != null) {
                log.debug("Adding CalcDBDesign: " + designId + " to the Location")
                calcDBDesignMap.remove(designId)
                JSONObject convertedDesign = convertCalcDBDesign(calcDBDesign)
                designs.add(convertedDesign)
            } else {
                log.debug("Could not find a CalcDBDesign with the id: " + designId)
            }
        }
        convertedLocation.put("designs", designs)
        String comments = "Location Imported from CalcDB. Date last modified in CalcDB was: " + convertedLocation.get("dateModified").toString()
        if (convertedLocation.containsKey("comments")) {
            comments = comments + "\n\n" + convertedLocation.getString("comments")
        }
        convertedLocation.put("comments", comments)

        [
            "_id",
            "clientFile",
            "clientFileVersion",
            "dateModified",
            "projectId",
            "projectLabel"
        ].each { convertedLocation.remove(it) }

        return convertedLocation
    }

    JSONObject createLocationJsonForDesign(CalcDBDesign calcDBDesign) {
        JSONObject designJson = calcDBDesign.getJSON()
        JSONObject locationObject = new JSONObject()
        String locationId = designJson.getString("locationLabel")
        if (locationId != null && !locationId.isEmpty()) {
            locationObject.put("id", locationId)
        }
        JSONObject coord = designJson.getJSONObject("geographicCoordinate")
        if (coord != null && !coord.isNullObject()) {
            locationObject.put("geographicCoordinate", coord)
        }

        JSONArray designsArray = new JSONArray()
        JSONObject convertedDesign = convertCalcDBDesign(calcDBDesign)
        designsArray.add(convertedDesign)
        locationObject.put("designs", designsArray)

        locationObject.put("comments", "Generated Location from CalcDB Design: " + calcDBDesign.getCalcDBId() + " uploaded on: " + designJson.get("dateModified").toString())
        return locationObject
    }

    JSONObject convertCalcDBDesign(CalcDBDesign calcDBDesign) {
        JSONObject convertedDesign = JSONObject.fromObject(calcDBDesign.getJSON())
        [
            "_id",
            "clientFile",
            "clientFileVersion",
            "dateModified",
            "locationId",
            "locationLabel",
            "projectId",
            "projectLabel",
            "worstAnchorResult",
            "worstCrossArmResult",
            "worstGuyResult",
            "worstInsulatorResult",
            "worstPoleResult",
            "worstSpanGuyResult"
        ].each { convertedDesign.remove(it) }
        convertedDesign.structure.keySet().each { String key ->
            Object structure = convertedDesign.structure.get(key)
            if (structure instanceof JSONArray) {
                structure.each { JSONObject instance ->
                    instance.remove("analysisResults")
                }
            } else {
                structure.remove("analysisResults")
            }
        }
        return convertedDesign
    }

    private String newPrimaryKey() {
        return new ObjectId().toString()
    }

    private Map<String, CalcDBProjectComponent> buildCalcDBIdMap(Collection<CalcDBProjectComponent> components) {
        return components.collectEntries{ [(it.calcDBId):it] }
    }
}
