package com.spidasoftware.schema.conversion

import net.sf.json.JSONObject
import net.sf.json.JSONArray
import org.apache.log4j.Logger
import org.bson.types.ObjectId

//import java.text.ParseException
//import java.text.SimpleDateFormat

class FormatConverter {
    private static final def log = Logger.getLogger(this)

    static Collection<CalcDBProjectComponent> convertCalcProject(Map calcProject) {
        List<CalcDBProjectComponent> components = []
        addDBIdsToProject(calcProject)
	    JSONObject referencedProject = new JSONObject()
	    referencedProject.put('id', calcProject.id)
	    referencedProject.put("dateModified", new Date().time)

	    //Project JSON that will get added to the referencedProject
	    JSONObject convertedProject = JSONObject.fromObject(calcProject)
	    convertedProject.leads.each{lead->
		    lead.locations.each{location->
			    components.addAll(convertCalcLocation(location, calcProject))

			    //clear out everything except the label and id
			    //These will be kept as references to the child components
			    def locId = location.id
			    def locLabel = location.label
			    location.clear()
			    location.put("id", locId)
			    location.put("label", locLabel)

		    }
	    }
		referencedProject.put("calcProject", convertedProject)
        components.add(new CalcDBProject(referencedProject))
        return components
    }

    static Collection<CalcDBProjectComponent> convertCalcLocation(Map calcLocation) {
        return convertCalcLocation(calcLocation, null)
    }

    static Collection<CalcDBProjectComponent> convertCalcLocation(Map calcLocation, Map calcProject) {
        ArrayList<CalcDBProjectComponent> components = []
        addDBIdsToLocation(calcLocation)

	    JSONObject referencedLocation = new JSONObject()
	    referencedLocation.put("dateModified", new Date().time)
		referencedLocation.put('id', calcLocation.id)

	    if (calcProject) {
		    referencedLocation.put("projectLabel", calcProject.get("label"))
		    referencedLocation.put("projectId", calcProject.get("id"))
		    referencedLocation.put("clientFile", calcProject.get("clientFile"))
		    referencedLocation.put("clientFileVersion", calcProject.get("clientFileVersion"))
	    }

	    //the calc location that will get saved as part of the referenced location
        JSONObject convertedLocation = JSONObject.fromObject(calcLocation)
        convertedLocation.get("designs").each { design ->
            components.add(convertCalcDesign(design, calcLocation, calcProject))

	        //clear out all the properties of the converted location's design
	        //except for the id and label, which will be foreign key refs
	        def desId = design.id
	        def desLabel = design.label
	        design.clear()
	        design.id = desId
	        design.label = desLabel
        }
		referencedLocation.put('calcLocation', convertedLocation)

        components.add(new CalcDBLocation(referencedLocation))
        return components
    }

    static CalcDBDesign convertCalcDesign(Map calcDesign) {
        return convertCalcDesign(calcDesign, null)
    }

    static CalcDBDesign convertCalcDesign(Map calcDesign, Map calcLocation) {
        return convertCalcDesign(calcDesign, calcLocation, null)
    }

    static CalcDBDesign convertCalcDesign(Map calcDesign, Map calcLocation, Map calcProject) {
        addDBIdToDesign(calcDesign)

	    JSONObject referencedDesign = new JSONObject()
	    referencedDesign.put("dateModified", new Date().time)
		referencedDesign.put('id', calcDesign.id)

	    if (calcLocation){
		    referencedDesign.put("locationLabel", calcLocation.get("label").toString())
		    referencedDesign.put("locationId", calcLocation.get("id").toString())
	    }
	    if (calcProject){
		    referencedDesign.put("projectLabel", calcProject.get("label"))
		    referencedDesign.put("projectId", calcProject.get("id").toString())
		    referencedDesign.put("clientFile", calcProject.get("clientFile"))
		    referencedDesign.put("clientFileVersion", calcProject.get("clientFileVersion"))
	    }

	    JSONObject convertedDesign = JSONObject.fromObject(calcDesign)
		referencedDesign.put("calcDesign", convertedDesign)
        addAnalysisResultsToNewDesign(calcDesign, referencedDesign)

        return new CalcDBDesign(referencedDesign)
    }

    private static void addAnalysisResultsToNewDesign(Map originalDesignObject, Map referencedDesign) {
        if (!originalDesignObject.containsKey("analysis")){
            log.trace("Design: ${originalDesignObject.label} does not contain any analysis results")
            return
        }

	    //create the container for the worst-case results
	    referencedDesign.worstCaseAnalysisResults = new JSONObject()

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
            def componentList = originalDesignObject.structure."$type"
            if (componentList && !componentList.isEmpty()){

                List allResultsForType = []
                componentList.each { component ->
                    List componentResults = getResultsForComponent(component.id, allOriginalAnalysisResults)
                    allResultsForType.addAll(componentResults)
                }

                def worstResultForType = getWorstResult(allResultsForType)
                if (worstResultForType){
                    // generate the property name for the new design json (i.e. 'worstAnchorResult')
                    referencedDesign.worstCaseAnalysisResults.put(type, worstResultForType)
                }
            }
        }

        // now to get the results for the Pole
        List allPoleResults = collectPoleResults(allOriginalAnalysisResults)

        if (!allPoleResults.isEmpty()){
            referencedDesign.worstCaseAnalysisResults.put("pole", getWorstResult(allPoleResults))
        } else {
            log.warn("Design ${originalDesignObject.label} has analysis results, but no results exist for the Pole")
        }
    }

    private static List collectPoleResults(List originalAnalysisResults){
        List allPoleResults = []
        originalAnalysisResults.each { loadCase ->
            def poleResults = loadCase.results.findAll { it.component.startsWith("Pole") }
            allPoleResults.addAll(poleResults)
        }
        return allPoleResults
    }

    private static JSONObject getWorstResult(List resultsArray){
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


    private static JSONArray getResultsForComponent(String componentId, List analysisList) {
        JSONArray results = new JSONArray()
        analysisList.each { loadCase ->
            loadCase.get("results").each { result ->
                JSONObject resultObject = (JSONObject) result
                if (resultObject.get("component") == componentId) {
                    results.add(resultObject)
                }
            }
        }
        return results
    }

    private static Map addDBIdsToProject(Map calcProject){
        calcProject.leads.each { lead ->
            lead.locations.each { location ->
                addDBIdsToLocation(location)
            }
        }
        return addDBId(calcProject)
    }

    private static Map addDBIdsToLocation(Map calcLocation) {
        calcLocation.designs.each { design ->
            addDBIdToDesign(design)
        }
        return addDBId(calcLocation)
    }

    private static Map addDBIdToDesign(Map design){
        return addDBId(design)
    }

    private static Map addDBId(Map thing) {
        if (!thing.containsKey("id")){
            thing.id = newPrimaryKey()
        }
        return thing
    }

    static JSONObject convertCalcDBProject(CalcDBProject calcDBProject, Collection<CalcDBLocation> calcDBLocations, Collection<CalcDBDesign> calcDBDesigns) {
        Map<String, CalcDBLocation> calcDBLocationMap = buildCalcDBIdMap(calcDBLocations)
        Map<String, CalcDBDesign> calcDBDesignMap = buildCalcDBIdMap(calcDBDesigns)
        return convertCalcDBProject(calcDBProject, calcDBLocationMap, calcDBDesignMap)
    }

    static JSONObject convertCalcDBProject(CalcDBProject calcDBProject, Map<String,CalcDBLocation> calcDBLocationMap, Map<String, CalcDBDesign> calcDBDesignMap) {
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

    static JSONObject convertCalcDBLocation(CalcDBLocation calcDBLocation, Collection<CalcDBDesign> calcDBDesigns) {
        Map<String, CalcDBDesign> calcDBDesignMap = buildCalcDBIdMap(calcDBDesigns)
        return convertCalcDBLocation(calcDBLocation, calcDBDesignMap)
    }

    static JSONObject convertCalcDBLocation(CalcDBLocation calcDBLocation, Map<String, CalcDBDesign> calcDBDesignMap) {
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

    static JSONObject createLocationJsonForDesign(CalcDBDesign calcDBDesign) {
        JSONObject designJson = calcDBDesign.getJSON()
        JSONObject locationObject = new JSONObject()
        String locationId = designJson.get("locationLabel")
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

    static JSONObject convertCalcDBDesign(CalcDBDesign calcDBDesign) {
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

    private static String newPrimaryKey() {
        return new ObjectId().toString()
    }

    private static Map<String, CalcDBProjectComponent> buildCalcDBIdMap(Collection<CalcDBProjectComponent> components) {
        return components.collectEntries{ [(it.calcDBId):it] }
    }
}
