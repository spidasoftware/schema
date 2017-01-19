package com.spidasoftware.schema.conversion

import net.sf.json.JSONObject
import net.sf.json.JSONArray
import org.apache.log4j.Logger
import org.bson.types.ObjectId

//import java.text.ParseException
//import java.text.SimpleDateFormat

class FormatConverter {
    private static final def log = Logger.getLogger(this)
    public static final double INFINITE_RESULT = Double.MAX_VALUE

     Collection<CalcDBProjectComponent> convertCalcProject(Map calcProject) {
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

     Collection<CalcDBProjectComponent> convertCalcLocation(Map calcLocation) {
        return convertCalcLocation(calcLocation, null)
    }

     Collection<CalcDBProjectComponent> convertCalcLocation(Map calcLocation, Map calcProject) {
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

     CalcDBDesign convertCalcDesign(Map calcDesign) {
        return convertCalcDesign(calcDesign, null)
    }

     CalcDBDesign convertCalcDesign(Map calcDesign, Map calcLocation) {
        return convertCalcDesign(calcDesign, calcLocation, null)
    }

     CalcDBDesign convertCalcDesign(Map calcDesign, Map calcLocation, Map calcProject) {
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
            loadCase.results.each { JSONObject resultObject ->
                if(resultObject.containsKey("component") && resultObject.getString("component").startsWith("Pole")) { // Old pre v4 analysis summary object
                    allPoleResults.add(resultObject)
                } else { // TODO: Test this
                    resultObject.get("components").each { JSONObject componentResult ->
                        if(componentResult.get("id") == "Pole") {
                            allPoleResults.add(convertDetailedResultToSummaryResults(resultObject, componentResult))
                        }
                    }
                }
            }
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

            if (normalizedResult < worstNormalizedResult) {
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
               if(resultObject.containsKey("component")) { // Old pre v4 analysis summary object
                   if (resultObject.get("component") == componentId) {
                       results.add(resultObject)
                   }
               } else {
                   resultObject.get("components").each { JSONObject componentResult ->
                       if(componentResult.get("id") == componentId) {
                           results.add(convertDetailedResultToSummaryResults(resultObject, componentResult))
                       }
                   }
               }
           }
       }
       return results
    }

    public static JSONObject convertDetailedResultToSummaryResults(JSONObject loadCaseResults, JSONObject componentResult) {
        JSONObject analysisAsset = new JSONObject()
        double actual = componentResult.getDouble("actual")
        actual = Math.min(INFINITE_RESULT, actual)
        analysisAsset.put("actual", actual);
        analysisAsset.put("allowable", componentResult.getDouble("allowable"))
        String resultsType = componentResult.getString("resultsType") == "PERCENT_LOADED" ? "PERCENT" : "SF"
        analysisAsset.put("unit", resultsType)
        analysisAsset.put("analysisDate", loadCaseResults.get("analysisDate"))
        analysisAsset.put("component", componentResult.getString("id"))
        analysisAsset.put("loadInfo", loadCaseResults.getString("analysisCase"))
        analysisAsset.put("passes", componentResult.getString("status") == "PASSING")
        analysisAsset.put("analysisType", componentResult.getString("analysisType"))
        return analysisAsset
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

     JSONObject convertCalcDBProject(CalcDBProject calcDBProject, Collection<CalcDBLocation> calcDBLocations, Collection<CalcDBDesign> calcDBDesigns) {
        Map<String, CalcDBLocation> calcDBLocationMap = buildCalcDBIdMap(calcDBLocations)
        Map<String, CalcDBDesign> calcDBDesignMap = buildCalcDBIdMap(calcDBDesigns)
        return convertCalcDBProject(calcDBProject, calcDBLocationMap, calcDBDesignMap)
    }

     JSONObject convertCalcDBProject(CalcDBProject calcDBProject, Map<String,CalcDBLocation> calcDBLocationMap, Map<String, CalcDBDesign> calcDBDesignMap) {
        // new project json object that we can keep adding to
        JSONObject convertedProject = JSONObject.fromObject(calcDBProject.getCalcJSON())

        // first match up projects with their child locations and designs
        List<String> locationIds = calcDBProject.getChildLocationIds()

	    JSONArray newLeadsArray = new JSONArray()

	    //first get all the child locations that are referenced in the project
	    convertedProject.getJSONArray('leads').each{JSONObject leadStub->
		    JSONArray calcLocations = new JSONArray()

		    log.trace("iterating through locations in the referenced lead to look for matches in the calcDBLocationMap")
		    leadStub.getJSONArray('locations').each { JSONObject locationStub ->
			    if (calcDBLocationMap.containsKey(locationStub.id)) {
				    log.debug("Adding location: ${locationStub.id} to the converted project")
				    calcLocations.add(convertCalcDBLocation(calcDBLocationMap.get(locationStub.id), calcDBDesignMap))
				    //remove the location from the map since it's been used
				    calcDBLocationMap.remove(locationStub.id)
			    }
		    }

		    //If any locations were added, then add the new lead
		    if (!calcLocations.isEmpty()) {
			    log.trace("Adding lead to the calc project")
			    JSONObject calcLead = new JSONObject()
			    if (leadStub.containsKey('label')) {
				    calcLead.put('label', leadStub.label)
			    }
			    calcLead.put('locations', calcLocations)
			    newLeadsArray.add(calcLead)
		    }
	    }


	    // now go through whatever locations and designs are left (not members of a selected project
	    JSONArray extraCalcLocations = new JSONArray() //will get added as the 'locations' in a lead

	    calcDBLocationMap.values().each { CalcDBLocation location ->
		    log.debug("adding orphaned CalcDB Location: ${location.getCalcDBId()}")
	        JSONObject locationJson = convertCalcDBLocation(location, calcDBDesignMap)
	        extraCalcLocations.add(locationJson)
		    calcDBLocationMap.remove(location.getCalcDBId())
	    }

	    // finally, for whatever designs are left without a location, add the design to a new location
        calcDBDesignMap.values().each { CalcDBDesign design ->
            log.debug("Adding orphaned CalcDB Design: " + design.calcDBId)
            JSONObject locationFromDesign = createLocationJsonForDesign(design)
            calcDBDesignMap.remove(design.calcDBId)
            extraCalcLocations.add(locationFromDesign)
        }

	    if (!extraCalcLocations.isEmpty()) {
		    JSONObject extLead = new JSONObject()
		    extLead.put('locations', extraCalcLocations)
		    newLeadsArray.add(extLead)
	    }

	    convertedProject.put('leads', newLeadsArray)

	    log.debug("Finished Converting CalcDB components to Calc project. Remaining locations: ${calcDBLocationMap.size()}, designs: ${calcDBDesignMap.size()}")
        return convertedProject
    }

     JSONObject convertCalcDBLocation(CalcDBLocation calcDBLocation, Collection<CalcDBDesign> calcDBDesigns) {
        Map<String, CalcDBDesign> calcDBDesignMap = buildCalcDBIdMap(calcDBDesigns)
        return convertCalcDBLocation(calcDBLocation, calcDBDesignMap)
    }

	/**
	 * Will convert the calcDBLocation to a calc location. Any Designs that are referenced by the location AND are contained in the
	 * <code>calcDBDesignMap</code> will be added to the Location and removed from the design map. Any extra designs in the design map
	 * will be ignored. This behavior is different from how things are handled in the `convertCalcDBProject` method, which will add extra
	 * locations and designs.
	 *
	 * @param calcDBLocation map of calcdbId to CalcDBLocation
	 * @param calcDBDesignMap map of calcDBId to CalcDBDesign
	 * @return a calc Location JSONObject that will be valid agians the location schema
	 */
     JSONObject convertCalcDBLocation(CalcDBLocation calcDBLocation, Map<String, CalcDBDesign> calcDBDesignMap) {
        log.debug("Adding CalcDBLocation: " + calcDBLocation.getName())
        JSONObject convertedLocation = JSONObject.fromObject(calcDBLocation.getCalcJSON())

        JSONArray convertedDesigns = new JSONArray()
        calcDBLocation.getDesignIds().each { String designId ->
            CalcDBDesign calcDBDesign = calcDBDesignMap.get(designId)
            if (calcDBDesign != null) {
                log.debug("Adding CalcDBDesign: " + designId + " to the Location")
                calcDBDesignMap.remove(designId)
                JSONObject convertedDesign = convertCalcDBDesign(calcDBDesign)
                convertedDesigns.add(convertedDesign)
            } else {
                log.debug("Could not find a CalcDBDesign with the id: " + designId)
            }
        }
        convertedLocation.put("designs", convertedDesigns)

        return convertedLocation
    }

     JSONObject createLocationJsonForDesign(CalcDBDesign calcDBDesign) {
        JSONObject locationObject = new JSONObject()
        String locationLabel = calcDBDesign.getParentLocationName()
        if (locationLabel != null && !locationLabel.isEmpty()) {
            locationObject.put("label", locationLabel)
        }

        JSONArray designsArray = new JSONArray()
        JSONObject convertedDesign = convertCalcDBDesign(calcDBDesign)
        designsArray.add(convertedDesign)
        locationObject.put("designs", designsArray)

        locationObject.put("comments", "Generated Location from CalcDB Design: " + calcDBDesign.getCalcDBId() + " uploaded on: " + calcDBDesign.getDateModified())
        return locationObject
    }

     JSONObject convertCalcDBDesign(CalcDBDesign calcDBDesign) {
        return JSONObject.fromObject(calcDBDesign.getCalcJSON())
    }

    private static String newPrimaryKey() {
        return new ObjectId().toString()
    }

    private static Map<String, CalcDBProjectComponent> buildCalcDBIdMap(Collection<CalcDBProjectComponent> components) {
        return components.collectEntries{ [(it.calcDBId):it] }
    }
}
