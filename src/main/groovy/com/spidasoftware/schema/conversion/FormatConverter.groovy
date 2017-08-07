package com.spidasoftware.schema.conversion

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.util.logging.Log4j
import org.bson.types.ObjectId

@Log4j
class FormatConverter {

    public static final double INFINITE_RESULT = Double.MAX_VALUE
    private static final String LOCATION_SCHEMA_PATH = "/schema/spidacalc/calc/location.schema"

    Collection<CalcDBProjectComponent> convertCalcProject(Map calcProject) {
        List<CalcDBProjectComponent> components = []
        addDBIdsToProject(calcProject)
        Map referencedProject = [:]
        referencedProject.put('id', calcProject.id)
        referencedProject.put("dateModified", new Date().time)

        //Project JSON that will get added to the referencedProject
        Map convertedProject = ChangeSet.duplicateAsJson(calcProject)
        convertedProject.leads.each { lead ->
            lead.locations.each { location ->
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

        Map referencedLocation = [:]
        referencedLocation.put("dateModified", new Date().time)
        referencedLocation.put('id', calcLocation.id)

        if (calcProject) {
            referencedLocation.put("projectLabel", calcProject.get("label"))
            referencedLocation.put("projectId", calcProject.get("id"))
            referencedLocation.put("clientFile", calcProject.get("clientFile"))
            referencedLocation.put("clientFileVersion", calcProject.get("clientFileVersion"))
        }

        //the calc location that will get saved as part of the referenced location
        Map convertedLocation = ChangeSet.duplicateAsJson(calcLocation)
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

        Map referencedDesign = [:]
        referencedDesign.put("dateModified", new Date().time)
        referencedDesign.put('id', calcDesign.id)

        if (calcLocation) {
            referencedDesign.put("locationLabel", calcLocation.get("label").toString())
            referencedDesign.put("locationId", calcLocation.get("id").toString())
        }
        if (calcProject) {
            referencedDesign.put("projectLabel", calcProject.get("label"))
            referencedDesign.put("projectId", calcProject.get("id").toString())
            referencedDesign.put("clientFile", calcProject.get("clientFile"))
            referencedDesign.put("clientFileVersion", calcProject.get("clientFileVersion"))
        }

        Map convertedDesign = ChangeSet.duplicateAsJson(calcDesign)
        referencedDesign.put("calcDesign", convertedDesign)
        addAnalysisResultsToNewDesign(calcDesign, referencedDesign)

        return new CalcDBDesign(referencedDesign)
    }

    private static void addAnalysisResultsToNewDesign(Map originalDesignObject, Map referencedDesign) {
        if (!originalDesignObject.containsKey("analysis")) {
            log.trace("Design: ${originalDesignObject.label} does not contain any analysis results")
            return
        }

        //create the container for the worst-case results
        referencedDesign.worstCaseAnalysisResults = [:]

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
            if (componentList && !componentList.isEmpty()) {

                List allResultsForType = []
                componentList.each { component ->
                    List componentResults = getResultsForComponent(component.id, allOriginalAnalysisResults)
                    allResultsForType.addAll(componentResults)
                }

                def worstResultForType = getWorstResult(allResultsForType)
                if (worstResultForType) {
                    // generate the property name for the new design json (i.e. 'worstAnchorResult')
                    referencedDesign.worstCaseAnalysisResults.put(type, worstResultForType)
                }
            }
        }

        // now to get the results for the Pole
        List allPoleResults = collectPoleResults(allOriginalAnalysisResults)

        if (!allPoleResults.isEmpty()) {
            referencedDesign.worstCaseAnalysisResults.put("pole", getWorstResult(allPoleResults))
        } else {
            log.warn("Design ${originalDesignObject.label} has analysis results, but no results exist for the Pole")
        }
    }

    private static List collectPoleResults(List originalAnalysisResults) {
        List allPoleResults = []
        originalAnalysisResults.each { loadCase ->
            loadCase.results.each { Map resultObject ->
                if (resultObject.containsKey("component") && resultObject.get("component").startsWith("Pole")) {
                    // Old pre v4 analysis summary object
                    allPoleResults.add(resultObject)
                } else {
                    resultObject.get("components").each { Map componentResult ->
                        if (componentResult.get("id") == "Pole") {
                            allPoleResults.add(convertDetailedResultToSummaryResults(resultObject, componentResult))
                        }
                    }
                }
            }
        }
        return allPoleResults
    }

    protected static Map getWorstResult(List resultsArray) {
        Map worstResult = resultsArray?.size() > 0 ? resultsArray[0] : null
        Double worstNormalizedResult = worstResult ? getNormalizedResult(worstResult) : null
        resultsArray.each { result ->
            def normalizedResult = getNormalizedResult(result)
            // If SF and allowable is 0 or PERCENT and actual is 0 getNormalizedResult returns null
            if (normalizedResult != null && (worstNormalizedResult == null || normalizedResult < worstNormalizedResult)) {
                worstNormalizedResult = normalizedResult
                worstResult = ChangeSet.duplicateAsJson(result)
            }
        }
        return worstResult
    }

    /*
     * Since results can be either SF or PERCENT, we'll have to check the unit
     * before we compare the relative 'badness' of each result (how does the actual
     * compare to the allowable). Strength results are a special case because even
     * though the unit is PERCENT, lower numbers mean a worse result. This is the
     * opposite of any other % based analysis result.
     * This will return null if the unit is SF and the allowable is 0 (Always passes)
     * This will return null if the unit is PERCENT and the actual is 0 (No load)
     */
    private static def getNormalizedResult(Map result) {
        def normalizedResult
        def isSFResult = result.unit == "SF" || result.component == "Pole-Strength"
        def isPercentResult = !isSFResult

        if (isSFResult && result.get("allowable") != 0) {
            normalizedResult = result.get("actual") / result.get("allowable")
        } else if(isPercentResult && result.get("actual") != 0) {
            normalizedResult = result.get("allowable") / result.get("actual")
        }
        return normalizedResult
    }


    private static List getResultsForComponent(String componentId, List analysisList) {
        List results = []
        analysisList.each { loadCase ->
            loadCase.get("results").each { result ->
                Map resultObject = (Map) result
                if (resultObject.containsKey("component")) { // Old pre v4 analysis summary object
                    if (resultObject.get("component") == componentId) {
                        results.add(resultObject)
                    }
                } else {
                    resultObject.get("components").each { Map componentResult ->
                        if (componentResult.get("id") == componentId) {
                            results.add(convertDetailedResultToSummaryResults(resultObject, componentResult))
                        }
                    }
                }
            }
        }
        return results
    }

    public
    static Map convertDetailedResultToSummaryResults(Map loadCaseResults, Map componentResult) {
        Map analysisAsset = [:]
        double actual = componentResult.get("actual")
        actual = Math.min(INFINITE_RESULT, actual)
        analysisAsset.put("actual", actual);
        analysisAsset.put("allowable", componentResult.get("allowable"))
        String resultsType = componentResult.get("resultsType") == "PERCENT_LOADED" ? "PERCENT" : "SF"
        analysisAsset.put("unit", resultsType)
        analysisAsset.put("analysisDate", loadCaseResults.get("analysisDate"))
        analysisAsset.put("component", componentResult.get("id"))
        analysisAsset.put("loadInfo", loadCaseResults.get("analysisCase"))
        analysisAsset.put("passes", componentResult.get("status") == "PASSING")
        analysisAsset.put("analysisType", componentResult.get("analysisType"))
        return analysisAsset
    }

    private static Map addDBIdsToProject(Map calcProject) {
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

    private static Map addDBIdToDesign(Map design) {
        return addDBId(design)
    }

    private static Map addDBId(Map thing) {
        if (!thing.containsKey("id")) {
            thing.id = newPrimaryKey()
        }
        return thing
    }

    Map convertCalcDBProject(CalcDBProject calcDBProject, Collection<CalcDBLocation> calcDBLocations, Collection<CalcDBDesign> calcDBDesigns) {
        Map<String, CalcDBLocation> calcDBLocationMap = buildCalcDBIdMap(calcDBLocations)
        Map<String, CalcDBDesign> calcDBDesignMap = buildCalcDBIdMap(calcDBDesigns)
        return convertCalcDBProject(calcDBProject, calcDBLocationMap, calcDBDesignMap)
    }

    Map convertCalcDBProject(CalcDBProject calcDBProject, Map<String, CalcDBLocation> calcDBLocationMap, Map<String, CalcDBDesign> calcDBDesignMap) {
        // new project json object that we can keep adding to
        Map convertedProject = ChangeSet.duplicateAsJson(calcDBProject.getCalcJSON())

        // first match up projects with their child locations and designs
        List<String> locationIds = calcDBProject.getChildLocationIds()

        List newLeadsArray = []

        //first get all the child locations that are referenced in the project
        convertedProject.get('leads').each { Map leadStub ->
            List calcLocations = []

            log.trace("iterating through locations in the referenced lead to look for matches in the calcDBLocationMap")
            leadStub.get('locations').each { Map locationStub ->
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
                Map calcLead = [:]
                if (leadStub.containsKey('label')) {
                    calcLead.put('label', leadStub.label)
                }
                calcLead.put('locations', calcLocations)
                newLeadsArray.add(calcLead)
            }
        }

        // now go through whatever locations and designs are left (not members of a selected project
        List extraCalcLocations = [] //will get added as the 'locations' in a lead

        calcDBLocationMap.values().each { CalcDBLocation location ->
            log.debug("adding orphaned CalcDB Location: ${location.getCalcDBId()}")
            Map locationJson = convertCalcDBLocation(location, calcDBDesignMap)
            extraCalcLocations.add(locationJson)
            calcDBLocationMap.remove(location.getCalcDBId())
        }

        // finally, for whatever designs are left without a location, add the design to a new location
        calcDBDesignMap.values().each { CalcDBDesign design ->
            log.debug("Adding orphaned CalcDB Design: " + design.calcDBId)
            Map locationFromDesign = createLocationJsonForDesign(design)
            calcDBDesignMap.remove(design.calcDBId)
            extraCalcLocations.add(locationFromDesign)
        }

        if (!extraCalcLocations.isEmpty()) {
            Map extLead = [:]
            extLead.put('locations', extraCalcLocations)
            newLeadsArray.add(extLead)
        }

        convertedProject.put('leads', newLeadsArray)

        log.debug("Finished Converting CalcDB components to Calc project. Remaining locations: ${calcDBLocationMap.size()}, designs: ${calcDBDesignMap.size()}")
        return convertedProject
    }

    Map convertCalcDBLocation(CalcDBLocation calcDBLocation, Collection<CalcDBDesign> calcDBDesigns) {
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
    Map convertCalcDBLocation(CalcDBLocation calcDBLocation, Map<String, CalcDBDesign> calcDBDesignMap) {
        log.debug("Adding CalcDBLocation: " + calcDBLocation.getName())
        Map convertedLocation = ChangeSet.duplicateAsJson(calcDBLocation.getCalcJSON())

        List convertedDesigns = []
        calcDBLocation.getDesignIds().each { String designId ->
            CalcDBDesign calcDBDesign = calcDBDesignMap.get(designId)
            if (calcDBDesign != null) {
                log.debug("Adding CalcDBDesign: " + designId + " to the Location")
                calcDBDesignMap.remove(designId)
                Map convertedDesign = convertCalcDBDesign(calcDBDesign)
                convertedDesigns.add(convertedDesign)
            } else {
                log.debug("Could not find a CalcDBDesign with the id: " + designId)
            }
        }
        convertedLocation.put("designs", convertedDesigns)

        return convertedLocation
    }

    Map createLocationJsonForDesign(CalcDBDesign calcDBDesign) {
        Map locationObject = [schema: LOCATION_SCHEMA_PATH]
        String locationLabel = calcDBDesign.getParentLocationName()
        if (locationLabel != null && !locationLabel.isEmpty()) {
            locationObject.put("label", locationLabel)
        }

        List designsArray = []
        Map convertedDesign = convertCalcDBDesign(calcDBDesign)
        if(convertedDesign.version){
            locationObject.version = convertedDesign.version
        }
        designsArray.add(convertedDesign)
        locationObject.put("designs", designsArray)

        locationObject.put("comments", "Generated Location from CalcDB Design: " + calcDBDesign.getCalcDBId() + " uploaded on: " + calcDBDesign.getDateModified())
        return locationObject
    }

    Map convertCalcDBDesign(CalcDBDesign calcDBDesign) {
        return ChangeSet.duplicateAsJson(calcDBDesign.getCalcJSON())
    }

    private static String newPrimaryKey() {
        return new ObjectId().toString()
    }

    private static Map<String, CalcDBProjectComponent> buildCalcDBIdMap(Collection<CalcDBProjectComponent> components) {
        return components.collectEntries { [(it.calcDBId): it] }
    }
}
