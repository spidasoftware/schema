/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.util.logging.Slf4j
import org.bson.types.ObjectId
import com.spidasoftware.utils.json.JsonIO

@Slf4j
class FormatConverter {

    public static final double INFINITE_RESULT = Double.MAX_VALUE
    private static final String LOCATION_SCHEMA_PATH = "/schema/spidacalc/calc/location.schema"

    /**
     *
     * @param calcProject
     * @param resultsFiles
     * @return
     */
    Collection<SpidaDBProjectComponent> convertCalcProject(Map calcProject, List<File> resultsFiles = null) {
        List<SpidaDBProjectComponent> components = []
        addDBIdsToProject(calcProject)
        Map referencedProject = [:]
        referencedProject.put('id', calcProject.id)
        referencedProject.put("dateModified", new Date().time)

        //Project JSON that will get added to the referencedProject
        Map convertedProject = CalcProjectChangeSet.duplicateAsJson(calcProject)

        convertedProject.leads.each { lead ->
            lead.locations.each { location ->
                components.addAll(convertCalcLocation(location, calcProject, resultsFiles))
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
        components.add(new SpidaDBProject(referencedProject))
        return components
    }

    Collection<SpidaDBProjectComponent> convertCalcLocation(Map calcLocation) {
        return convertCalcLocation(calcLocation, null, null)
    }

    /**
     *
     * @param calcLocation
     * @param calcProject
     * @param resultsFiles
     * @return
     */
    Collection<SpidaDBProjectComponent> convertCalcLocation(Map calcLocation, Map calcProject, List<File> resultsFiles) {
        ArrayList<SpidaDBProjectComponent> components = []
        addDBIdsToLocation(calcLocation)

        Map referencedLocation = [:]
        referencedLocation.put("dateModified", new Date().time)
        referencedLocation.put('id', calcLocation.id)

        if (calcProject) {
            referencedLocation.put("projectLabel", calcProject.get("label"))
            referencedLocation.put("projectId", calcProject.get("id"))
            referencedLocation.put("clientFile", calcProject.get("clientFile"))
            if(calcProject.get("clientFileVersion")){
            	referencedLocation.put("clientFileVersion", calcProject.get("clientFileVersion"))
            }
        }

        //the calc location that will get saved as part of the referenced location
        Map convertedLocation = CalcProjectChangeSet.duplicateAsJson(calcLocation)
        convertedLocation.get("designs").each { design ->
            // if the design has analysis details then handle results
            if (design.containsKey("analysisDetails")) {
                /*
                if (design.get("analysisDetails").containsKey("resultId")) {
                    String resultId = design.get("analysisDetails").get("resultId")
                    File resultsFile = resultsFiles.find { File resultsFile -> resultsFile.name -  ~/\.\w+$/ == resultId }
                    //String resultsFileJSON = JsonIO.parse(resultsFile)
                }
                // results are included in json files
                if (resultsFiles) {
                    // find the file
                    //TODO: locate the file
                    // parse the file to a map
                    //TODO: parse json
                    // convert the calc result from the file
                    SpidaDBResult spidaDBResult = convertCalcResult(design.analysisDetails.detailedResults)
                    // add component to the return
                    components.add(spidaDBResult)
                    // set the id to the spida db id
                    design.analysisDetails.put("id", spidaDBResult.spidaDBId)
                } else {

                 */
                    // results are included in the locations map
                    if (design.get("analysisDetails").containsKey("detailedResults")) {
                        SpidaDBResult spidaDBResult = convertCalcResult(design.analysisDetails.detailedResults)
                        components.add(spidaDBResult)
                        design.analysisDetails.remove("detailedResults")
                        design.analysisDetails.put("id", spidaDBResult.spidaDBId)
                    }
                //}
            }
            SpidaDBDesign convertedDesign = convertCalcDesign(design, calcLocation, calcProject)
            components.add(convertedDesign)
            //clear out all the properties of the converted location's design
            //except for the id and label, which will be foreign key refs
            def desId = design.id
            def desLabel = design.label
            design.clear()
            design.id = desId
            design.label = desLabel
        }
        referencedLocation.put('calcLocation', convertedLocation)

        components.add(new SpidaDBLocation(referencedLocation))
        return components
    }

    SpidaDBDesign convertCalcDesign(Map calcDesign) {
        return convertCalcDesign(calcDesign, null)
    }

    SpidaDBDesign convertCalcDesign(Map calcDesign, Map calcLocation) {
        return convertCalcDesign(calcDesign, calcLocation, null)
    }

    SpidaDBDesign convertCalcDesign(Map calcDesign, Map calcLocation, Map calcProject) {
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
            if(calcProject.get("clientFileVersion")){
            	referencedDesign.put("clientFileVersion", calcProject.get("clientFileVersion"))
            }
        }

        Map convertedDesign = CalcProjectChangeSet.duplicateAsJson(calcDesign)
        referencedDesign.put("calcDesign", convertedDesign)
        addAnalysisResultsToNewDesign(calcDesign, referencedDesign)
        SpidaDBDesign dbDesign = new  SpidaDBDesign(referencedDesign)
        return dbDesign
    }

	SpidaDBResult convertCalcResult(Map calcResults) {
        Map referencedResults = [:]
        Map convertedResult = CalcProjectChangeSet.duplicateAsJson(calcResults)
        String dbid = new ObjectId().toString()
        convertedResult.put("dbId", dbid)
        referencedResults.put("calcResult", convertedResult)
        // a unique id is assigned to referenced results to prevent multiple designs referencing the same result id
        referencedResults.put("id", dbid)
        referencedResults.put("dateModified", new Date().time)
        return new SpidaDBResult(referencedResults)
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
            def componentList = originalDesignObject.structure?."$type"
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
                worstResult = CalcProjectChangeSet.duplicateAsJson(result)
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
        def isSFResult = result.unit == "SF" || (result.component == "Pole" && result.analysisType == "STRENGTH")
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

    protected static Map addDBIdsToProject(Map calcProject) {
        // first, we want to detect and clear any duplicate Ids in the project.

        def duplicateIds = [:].withDefault {[]}
        calcProject.leads.each {lead ->
            lead.locations.each { location ->
                duplicateIds[location.id] << location
                location.designs.each {design ->
                    duplicateIds[design.id] << design
                }
            }
        }
        duplicateIds.each {id, dupes ->
            if (id != null && dupes.size() > 1) {
                dupes.each {dupe ->
                    dupe.remove("id")
                }
            }
        }

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

    Map convertSpidaDBProject(SpidaDBProject spidaDBProject, Collection<SpidaDBLocation> spidaDBLocations, Collection<SpidaDBDesign> spidaDBDesigns, Collection<SpidaDBResult> spidaDBResult = []) {
        Map<String, SpidaDBLocation> spidaDBLocationMap = buildSpidaDBIdMap(spidaDBLocations)
        Map<String, SpidaDBDesign> spidaDBDesignMap = buildSpidaDBIdMap(spidaDBDesigns)
        Map<String, SpidaDBResult> spidaDBResultMap = buildSpidaDBIdMap(spidaDBResult)
        return convertSpidaDBProject(spidaDBProject, spidaDBLocationMap, spidaDBDesignMap, spidaDBResultMap)
    }

    Map convertSpidaDBProject(SpidaDBProject spidaDBProject, Map<String, SpidaDBLocation> spidaDBLocationMap, Map<String, SpidaDBDesign> spidaDBDesignMap, Map<String, SpidaDBResult> spidaDBResultMap = [:]) {
        // new project json object that we can keep adding to
        Map convertedProject = CalcProjectChangeSet.duplicateAsJson(spidaDBProject.getCalcJSON())

        // first match up projects with their child locations and designs
        List<String> locationIds = spidaDBProject.getChildLocationIds()

        List newLeadsArray = []

        //first get all the child locations that are referenced in the project
        convertedProject.get('leads').each { Map leadStub ->
            List calcLocations = []

            log.trace("iterating through locations in the referenced lead to look for matches in the spidaDBLocationMap")
            leadStub.get('locations').each { Map locationStub ->
                if (spidaDBLocationMap.containsKey(locationStub.id)) {
                    log.debug("Adding location: ${locationStub.id} to the converted project")
                    calcLocations.add(convertSpidaDBLocation(spidaDBLocationMap.get(locationStub.id), spidaDBDesignMap, spidaDBResultMap))
                    //remove the location from the map since it's been used
                    spidaDBLocationMap.remove(locationStub.id)
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

        spidaDBLocationMap.values().each { SpidaDBLocation location ->
            log.debug("adding orphaned SpidaDB Location: ${location.getSpidaDBId()}")
            Map locationJson = convertSpidaDBLocation(location, spidaDBDesignMap, spidaDBResultMap)
            extraCalcLocations.add(locationJson)
            spidaDBLocationMap.remove(location.getSpidaDBId())
        }

        // finally, for whatever designs are left without a location, add the design to a new location
        spidaDBDesignMap.values().each { SpidaDBDesign design ->
            log.debug("Adding orphaned SpidaDB Design: " + design.spidaDBId)
            Map locationFromDesign = createLocationJsonForDesign(design)
            spidaDBDesignMap.remove(design.spidaDBId)
            extraCalcLocations.add(locationFromDesign)
        }

        if (!extraCalcLocations.isEmpty()) {
            Map extLead = [:]
            extLead.put('locations', extraCalcLocations)
            newLeadsArray.add(extLead)
        }

        convertedProject.put('leads', newLeadsArray)

        log.debug("Finished Converting SpidaDB components to Calc project. Remaining locations: ${spidaDBLocationMap.size()}, designs: ${spidaDBDesignMap.size()}")
        return convertedProject
    }

    Map convertSpidaDBLocation(SpidaDBLocation spidaDBLocation, Collection<SpidaDBDesign> spidaDBDesigns, Collection<SpidaDBResult> spidaDBResults = []) {
        Map<String, SpidaDBDesign> spidaDBDesignMap = buildSpidaDBIdMap(spidaDBDesigns)
        Map<String, SpidaDBResult> spidaDBResultMap = buildSpidaDBIdMap(spidaDBResults)
        return convertSpidaDBLocation(spidaDBLocation, spidaDBDesignMap, spidaDBResultMap)
    }

    /**
     * Will convert the spidaDBLocation to a calc location. Any Designs that are referenced by the location AND are contained in the
     * <code>spidaDBDesignMap</code> will be added to the Location and removed from the design map. Any extra designs in the design map
     * will be ignored. This behavior is different from how things are handled in the `convertSpidaDBProject` method, which will add extra
     * locations and designs.
     *
     * @param spidaDBLocation map of spidadbId to SpidaDBLocation
     * @param spidaDBDesignMap map of spidaDBId to SpidaDBDesign
     * @return a calc Location JSONObject that will be valid agians the location schema
     */
    Map convertSpidaDBLocation(SpidaDBLocation spidaDBLocation, Map<String, SpidaDBDesign> spidaDBDesignMap, Map<String, SpidaDBResult> spidaDBResultMap = [:]) {
        log.debug("Adding SpidaDBLocation: " + spidaDBLocation.getName())
        Map convertedLocation = CalcProjectChangeSet.duplicateAsJson(spidaDBLocation.getCalcJSON())

        List convertedDesigns = []
        spidaDBLocation.getDesignIds().each { String designId ->
            SpidaDBDesign spidaDBDesign = spidaDBDesignMap.get(designId)
            if (spidaDBDesign != null) {
                log.debug("Adding SpidaDBDesign: " + designId + " to the Location")
                spidaDBDesignMap.remove(designId)
                Map convertedDesign = convertSpidaDBDesign(spidaDBDesign, spidaDBResultMap)
                convertedDesigns.add(convertedDesign)
            } else {
                log.debug("Could not find a SpidaDBDesign with the id: " + designId)
            }
        }
        convertedLocation.put("designs", convertedDesigns)

        return convertedLocation
    }

    Map createLocationJsonForDesign(SpidaDBDesign spidaDBDesign) {
        Map locationObject = [schema: LOCATION_SCHEMA_PATH]
        String locationLabel = spidaDBDesign.getParentLocationName()
        if (locationLabel != null && !locationLabel.isEmpty()) {
            locationObject.put("label", locationLabel)
        }

        List designsArray = []
        Map convertedDesign = convertSpidaDBDesign(spidaDBDesign)
        if(convertedDesign.version){
            locationObject.version = convertedDesign.version
        }
        designsArray.add(convertedDesign)
        locationObject.put("designs", designsArray)

        locationObject.put("comments", "Generated Location from SpidaDB Design: " + spidaDBDesign.getSpidaDBId() + " uploaded on: " + spidaDBDesign.getDateModified())
        return locationObject
    }

    Map convertSpidaDBDesign(SpidaDBDesign spidaDBDesign, Collection<SpidaDBResult> spidaDBResults) {
        Map<String, SpidaDBResult> spidaDBResultMap = buildSpidaDBIdMap(spidaDBResults)
        return convertSpidaDBDesign(spidaDBDesign, spidaDBResultMap)
    }

    Map convertSpidaDBDesign(SpidaDBDesign spidaDBDesign, Map<String, SpidaDBResult> spidaDBResultMap = [:]) {
        Map convertedDesign = CalcProjectChangeSet.duplicateAsJson(spidaDBDesign.getCalcJSON())
        if(!spidaDBResultMap.isEmpty()) {
            SpidaDBResult result = spidaDBResultMap.get(spidaDBDesign.resultDbId)
            String resultId = spidaDBDesign.resultId
            if (result != null) {
                log.debug("Adding SpidaDBResult: " + resultId + " to the Design")
                Map convertedResult = convertSpidaDBResult(result)
                spidaDBResultMap.remove(spidaDBDesign.resultId)
                convertedDesign.analysisDetails.put("detailedResults", convertedResult)
                convertedDesign.analysisDetails.put("id", result.spidaDBId)
            } else {
                log.debug("Could not find a SpidaDBResult with the id: " + resultId)
            }
        }

        return convertedDesign
    }

    Map convertSpidaDBResult(SpidaDBResult spidaDBResult) {
        return CalcProjectChangeSet.duplicateAsJson(spidaDBResult.getCalcJSON())
    }

    private static String newPrimaryKey() {
        return new ObjectId().toString()
    }

    private static Map<String, SpidaDBProjectComponent> buildSpidaDBIdMap(Collection<SpidaDBProjectComponent> components) {
        return components.collectEntries { [(it.spidaDBId): it] }
    }
}
