/*
 * ©2009-2021 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v9

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import com.spidasoftware.schema.utils.StringFormatting
import groovy.transform.CompileStatic

@CompileStatic
/**
 * Custom environments were introduced in version 8.0.
 *
 * When a client data is up converted, pre fill them with our old default environments.
 * Change all references of environments in the project to pretty print form of the original Enum.
 *
 * When a client data is down converted, check if name when converted to enum form, matches {@link Environment}.
 * If it does match an enum from {@link Environment}, then convert all references in the project to that enum.
 * If it does not match an enum from {@link Environment}, then convert all references in the project to NONE.
 */
class EnvironmentClientDataChangeset extends AbstractClientDataChangeSet {

	public static String NONE = "NONE"

	@Override
	boolean applyToClientData(Map clientDataJSON) throws ConversionException {
		List<Map> defaultEnvironmentMaps = createDefaultEnvironmentMaps(Environment.values().collect {it.toString()})
		clientDataJSON.put("environments", defaultEnvironmentMaps)
		return true
	}

	@Override
	void applyToProject(Map projectJSON) throws ConversionException {
		Set<String> projectEnvironments = []
		projectJSON.get("leads")?.each { Map leadJSON ->
			leadJSON.get("locations")?.each { Map locationJSON ->
				applyToLocation(locationJSON, projectEnvironments)
			}
		}
		if (projectJSON.containsKey("clientData")) {
			Map clientDataJSON = projectJSON.clientData as Map
			clientDataJSON.put("environments", createDefaultEnvironmentMaps(projectEnvironments))
		}
	}

	@Override
	void applyToLocation(Map locationJSON) throws ConversionException {
		applyToLocation(locationJSON, new HashSet<String>())
	}

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		applyToDesign(designJSON, new HashSet<String>())
	}

	@Override
	void applyToResults(Map resultsJSON) throws ConversionException {
		Set<String> environments = []
		if (resultsJSON.containsKey("analyzedStructure")) {
			environments = applyToStructure(resultsJSON.analyzedStructure as Map)
		}
		if (resultsJSON.containsKey("clientData")) {
			Map clientDataJSON = resultsJSON.clientData as Map
			clientDataJSON.put("environments", createDefaultEnvironmentMaps(environments))
			clientDataJSON.remove("hash")
		}
	}

	protected void applyToLocation(Map locationJSON, Set<String> projectEnvironments) throws ConversionException {
		locationJSON.get("designs")?.each { Map designJSON ->
			applyToDesign(designJSON, projectEnvironments)
		}
	}

	protected void applyToDesign(Map designJSON, Set<String> projectEnvironments) throws ConversionException {
		projectEnvironments.addAll(applyToStructure(designJSON.structure as Map))
		if (designJSON.containsKey("analysisDetails") && ((Map)designJSON.analysisDetails).containsKey("detailedResults")) {
			Map detailedResultsJSON = ((Map)designJSON.analysisDetails).detailedResults as Map
			applyToResults(detailedResultsJSON)
		}
	}

	/**
	 * @return set of environments from structure
	 */
	protected Set<String> applyToStructure(Map structureJSON) {
		Set<String> designEnvironments = new HashSet<>()
		applyPrettyPrintToEnvironment([structureJSON.pole] as List<Map>, designEnvironments)
		applyPrettyPrintToEnvironment(structureJSON.spanPoints as List<Map>, designEnvironments)
		applyPrettyPrintToEnvironment(structureJSON.wireEndPoints as List<Map>, designEnvironments)
		return designEnvironments
	}

	protected List<Map> createDefaultEnvironmentMaps(Collection<String> environments) {
		return environments
				.findAll{!StringFormatting.equalsIgnoreCaseAndEnumFormat(NONE, it)}
				.sort({it})
				.collect ({ [name: it, description: "N/A"] as Map})
	}

	protected void applyPrettyPrintToEnvironment(List<Map> environmentItems, Set<String> environments) {
		environmentItems.each { Map environmentItem ->
			String newEnvironment = StringFormatting.makeReadableString(environmentItem.environment as String)
			environmentItem.environment = newEnvironment
			environments.add(newEnvironment)
		}
	}

	@Override
	boolean revertClientData(Map clientDataJSON) throws ConversionException {
		clientDataJSON.remove("environments")
		return true
	}

	@Override
	void revertProject(Map projectJSON) throws ConversionException {
		projectJSON.remove("clientFileVersion")
		if (projectJSON.containsKey("clientData")) {
			Map clientDataJSON = projectJSON.clientData as Map
			revertClientData(clientDataJSON)
			clientDataJSON.remove("hash")
		}
		projectJSON.get("leads")?.each { Map leadJSON ->
			leadJSON.get("locations")?.each { Map locationJSON ->
				revertLocation(locationJSON)
			}
		}
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		if (designJSON.containsKey("analysisDetails") && ((Map)designJSON.analysisDetails).containsKey("detailedResults")) {
			Map detailedResultsJSON = ((Map)designJSON.analysisDetails).detailedResults as Map
			revertResults(detailedResultsJSON)
		}
		revertStructure(designJSON.structure as Map)
	}


	@Override
	void revertResults(Map resultsJSON) throws ConversionException {
		if (resultsJSON.containsKey("clientData")) {
			Map clientDataJSON = resultsJSON.clientData as Map
			if (revertClientData(clientDataJSON) && clientDataJSON.containsKey("hash")) {
				clientDataJSON.remove("hash")
			}
			revertStructure(resultsJSON.analyzedStructure as Map)
		}
	}

	protected void revertStructure(Map StructureJSON) {
		revertEnvironmentToEnumFormat(StructureJSON.pole as Map)
		(StructureJSON.spanPoints as List<Map>).each { Map spanPoint -> revertEnvironmentToEnumFormat(spanPoint)}
		(StructureJSON.wireEndPoints as List<Map>).each { Map wireEndPoint -> revertEnvironmentToEnumFormat(wireEndPoint)}
	}

	protected void revertEnvironmentToEnumFormat(Map environmentItem) {
		Environment defaultEnvironment = Environment.values().toList().find{StringFormatting.equalsIgnoreCaseAndEnumFormat(it.name(), environmentItem.environment as String)}
		if (defaultEnvironment != null) {
			environmentItem.put("environment", defaultEnvironment.name())
		} else {
			environmentItem.put("environment", NONE)
		}
	}
}
